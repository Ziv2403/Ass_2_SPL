package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	private boolean isDone;
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.isDone = false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public T get() {
		while (!isDone) {
			try{
				wait(); //until the result is resolved
			} catch(InterruptedException e){
				Thread.currentThread().interrupt(); //Restore the interrupted status
			}
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
    * @param result The result to set for this Future.
 	* @pre The Future must not have been previously resolved. The input result must not be null.
	* @post The result is set, and all waiting threads are notified. The isDone flag is set to true.
     */
	public synchronized void resolve (T result) {
		if (!isDone) {
			this.result = result;
			this.isDone = true;
			notifyAll(); // Notify all waiting threads
		}
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public synchronized boolean isDone() {
		return isDone;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
	 * @pre The timeout must be greater than zero, and the unit must not be null.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		// if (timeout <= 0 || unit == null) {
		// 	return null;  
		// } //NEED TO CHECK IF IT IS OUR RESPONSIBILITY TO VERIFY THE ACCUACY OF THE INPUT
		if (isDone) {
			return result;
		}
		try {
			long timeoutMillis = unit.toMillis(timeout); // $$Converts the given time to milliseconds, so that the wait is accurate$$
			wait(timeoutMillis);	
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore the interrupted status
		}
		return isDone ? result : null;
	}
}


/*$$Justification for using synchronized: More than one thread may want to access a method like resolve() or get() at the same time.
If one process sets the result (resolve) and another process tries to read it (get), there may be Raceconditions.
synchronized prevents these situations by only allowing one thread to enter a particular block of code at any given time.
Justification for wait() and notifyAll():
wait() stops the thread until a result is set (resolve), without wasting resources.
notifyAll() wakes up all waiting threads when the result is ready.
Conclusion: This method is efficient because it uses the operating system's mechanism for waiting rather than an infinite loop.$$*/
