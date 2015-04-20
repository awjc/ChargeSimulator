package main;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPoolTest {
	//Dummy class to hold ball information and update operation
	static class Ball {
		private int x;
		private int y;
		private int dx;
		private int dy;

		public Ball(int x, int y, int dx, int dy) {
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
		}

		public void update(List<Ball> balls){
			for(Ball b : balls){
				if(b != this){
					x += b.dx;
					y += 1;
				}
			}
		}

		public Dimension getPos(){
			return new Dimension(x, y);
		}

		@Override
		public String toString(){
			return String.format("Ball: (%d, %d)", x, y);
		}
	}

	public static void main(String[] args){
		//Params for algorithm
		final int NBALLS = 30_000;
		final int NTASKS = 4;

		//The call to synchronizedList() is important since the list is being accessed by multiple threads
		final List<Ball> balls = Collections.synchronizedList(new ArrayList<Ball>());

		//Populate the list of balls
		for(int i = 0; i < NBALLS; i++){
			balls.add(new Ball(10, 20, 5, 1));
		}

		System.out.println("INITIALIZED!");
		System.out.println();

		//Create a list of Runnable "tasks" to be executed
		List<Runnable> work = new ArrayList<>();
		
		//This is the number of balls to be processed per task
		int nBalls = balls.size() / NTASKS;
		
		//Divide up the list of balls and assign each segment to be processed in an individual task.
		for(int i = 0; i < NTASKS; i++){
			final int startBalls = i * nBalls;
			final int endBalls = i < NTASKS - 1 ? (i + 1) * nBalls - 1 : balls.size() - 1;
			final int workerNum = i + 1;
			System.out.println(startBalls + " : " + endBalls);
			
			//Create the task as an anonymous subclass of the Runnable interface so that
			//it can be executed and run. When the run() method has terminated, the task
			//and its associated thread will be terminated gracefully
			work.add(new Runnable(){
				@Override
				public void run(){
					//Just update all the balls - note that the update operation
					//references the list of balls
					for(int i = startBalls; i <= endBalls; i++){
						balls.get(i).update(balls);
					}

					System.out.println("Worker #" + workerNum + " finished!");
				}
			});
		}
		
		//Create an executor service with a certain number of threads. This number can be played with
		//but in order to get full utilization of CPU time this number seems to work pretty well
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
		for(int i = 0; i < 5; i++){	
			System.out.println(balls.get(0));
			long start = System.currentTimeMillis();
			
			//A list of "Future" objects that contain info on whether or not the task is finished
			//and any return values of the task
			List<Future<?>> futures = new ArrayList<>();
			
			//Tell the executor which tasks we wish to execute and add them to the list of futures
			for(Runnable r : work){
				
				//When you call .submit(), the task will begin to be executed on a new thread. This method
				//returns a future object that can be referenced later.
				futures.add(executor.submit(r));
			}

			
			//the call to f.get() blocks until the future's task has terminated. By calling f.get() 
			//on all the futures we ensure that all the tasks have completed.
			for(Future<?> f : futures){
					try{
						f.get();
					} catch(InterruptedException | ExecutionException e){
						e.printStackTrace();
					}
			}

			//We're done!
			System.out.println("ALL FINISHED! Total time: " + (System.currentTimeMillis() - start) / 1000.0
					+ " seconds");
		}
		System.out.println(balls.get(0));
		
		//Free any thread resources we may have been hogging
		executor.shutdown();
	}
}
