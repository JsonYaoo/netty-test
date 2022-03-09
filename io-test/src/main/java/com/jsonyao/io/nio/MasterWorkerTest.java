package com.jsonyao.io.nio;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Master-Worker模型:
 *      1、主线程负责把任务丢到Master#Queue;
 *      2、Master线程负责消费Master#Queue, 提交任务给Worker#Queue;
 *      3、Worker线程负责消费Worker#Queue, 计算好结果后调用Master传入的函数式接口回写结果到Master缓存
 */
public class MasterWorkerTest {

    static class SimpleTask extends Task<Integer> {
        // 11、任务多态执行
        protected Integer doExecute() {
            System.err.println("task" + id + " is done!");
            return id;
        }
    }

    public static void main(String[] args) {
        // 0、构建 1个Master, 4个worker
        Master<SimpleTask, Integer> master = new Master<>(4);

        // 5、定期2秒提交任务给Master->Worker执行
        ScheduledExecutorService run = Executors.newSingleThreadScheduledExecutor();
        run.scheduleAtFixedRate(() -> master.submit(new SimpleTask()), 2, 2, TimeUnit.SECONDS);

        // 15：定期5秒输出结果：输出sum结果, 以及缓存中每个Task的结果
        ScheduledExecutorService res = Executors.newSingleThreadScheduledExecutor();
        res.scheduleAtFixedRate(master::printResult, 5, 5, TimeUnit.SECONDS);
    }
}

class Task<R> {
    static AtomicInteger index = new AtomicInteger(1);
    public Consumer<Task<R>> resultAction;
    public int id;
    public int workerId;
    R result = null;

    public Task() {
        this.id = index.getAndIncrement();
    }

    public void execute() {
        // 10、任务多态执行
        this.result = this.doExecute();

        // 12、任务执行完毕, Consumer函数式接口回调
        resultAction.accept(this);
    }

    protected R doExecute() {
        return null;
    }
}

class Worker<T extends Task, R> {
    private LinkedBlockingQueue<T> taskQueue = new LinkedBlockingQueue<>();
    static AtomicInteger index = new AtomicInteger(1);
    private int workerId;
    private Thread thread = null;

    public Worker() {
        this.workerId = index.getAndIncrement();
        thread = new Thread(this::run);
        thread.start();
    }

    private void run() {
        for (; ; ) {
            try {
                // 2、阻塞获取Worker任务
                T task = this.taskQueue.take();

                // 9、Worker任务获取成功, 进行任务执行
                task.workerId = workerId;
                task.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void submit(T task, Consumer<R> action) {
        task.resultAction = action;
        try {
            // 8、提交Worker任务
            this.taskQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Master<T extends Task, R> {
    private HashMap<String, Worker<T, R>> workers = new HashMap<>();
    private LinkedBlockingQueue<T> taskQueue = new LinkedBlockingQueue<>();
    private Map<String, R> resultMap = new ConcurrentHashMap<>();
    private Thread thread = null;
    private AtomicLong sum = new AtomicLong(0);

    public Master(int workerCount) {
        for (int i = 0; i < workerCount; i++) {
            // 1、构建 Worker, 阻塞获取Worker#taskQueue
            Worker<T, R> worker = new Worker<>();
            workers.put("子节点: " + i, worker);
        }

        // 3、分发任务给Worker: 阻塞式获取Master任务
        thread = new Thread(this::execute);
        thread.start();
    }

    public void submit(T task) {
        // 6、添加Master任务到阻塞队列
        taskQueue.add(task);
    }

    // 13、任务执行完毕, Consumer函数式接口回调
    private void resultCallBack(Object o) {
        Task<R> task = (Task<R>) o;
        String taskName = "Worker: " + task.workerId + "-" + "Task: " + task.id;

        // 14、获取结果, 设置到缓存, 并完成累加操作
        R result = task.result;
        resultMap.put(taskName, result);
        sum.getAndAdd(Long.valueOf(String.valueOf(result)));
    }

    public void execute() {
        for(; ; ) {
            for (Map.Entry<String, Worker<T, R>> entry : workers.entrySet()) {
                T task = null;
                try {
                    // 4、阻塞式获取Master任务
                    task = this.taskQueue.take();

                    // 7、Master任务获取成功, 提交给Worker进行执行, 同时设置Consumer函数式接口
                    Worker<T, R> worker = entry.getValue();
                    worker.submit(task, this::resultCallBack);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void printResult() {
        // 16、输出sum结果
        System.err.println("sum is: " + sum.get());

        // 17：以及缓存中每个Task的结果
        for (Map.Entry<String, R> entry : resultMap.entrySet()) {
            String taskName = entry.getKey();
            System.err.println(taskName + ":" + entry.getValue());
        }
    }
}
