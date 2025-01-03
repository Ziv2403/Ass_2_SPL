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

	// --------------------- fields -------------------------

	private T result;
	private boolean isDone;	// MAYBE USE 'VOLATILE' HERE SO ALL THREADS CAN SEE THE STATE IN REAL TIME

    // --------------------- constructor --------------------

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.isDone = false;
	}

    // --------------------- methods ------------------------

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
	* 
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
     * Checks whether the Future object has been resolved.
     *
     * @return true if this object has been resolved, false otherwise.
     */
	public synchronized boolean isDone() {
		return isDone;
	}

    /**
     * Retrieves the result the Future object holds if it has been resolved.
     * This method is non-blocking and has a limited amount of wait time determined
     * by {@code timeout}.
     *
     * @param timeout the maximal amount of time units to wait for the result.
     * @param unit    the {@link TimeUnit} specifying the time unit for the timeout.
     * @pre The timeout must be greater than zero, and the unit must not be null.
     * @return the result of type T if it is available; otherwise, waits for {@code timeout} TimeUnits.
     *         Returns null if the time elapsed before the result became available.
     */
	public synchronized T get(long timeout, TimeUnit unit) {

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
