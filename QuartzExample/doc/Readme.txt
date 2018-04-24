====================================================================================================
example1
可以直接執行。
應該是安排一個偶數分鐘的執行的一個job，
但好像沒效，觀察好像是整數分鐘就執行。


StdSchedulerFactory

RAM-based scheduler

sched.scheduleJob(job, trigger);

sched.start();


sched.shutdown(true);
Note: passing true into the shutdown message tells the Quartz Scheduler to wait until all jobs have completed running before returning from the method call.


====================================================================================================
example2
可以直接執行。
展示各種安排job的方式。


同一個Job可以同時排給不同trigger。

Job可以在排程start()之前安排。
也可以在start()之後加入。

Job可以直接執行，不用trigger。(直接執行？重新執行？)

也可以重新安排。


====================================================================================================
example3
可以直接執行。
展示各種安排CronTrigger的方式。

用cron-expression來排定時間。
一種以日期為基礎的方式。


====================================================================================================
example4
展示Job保持狀態的方式。
Job的class property無法用來保持狀態值。
必須要用JobDataMap。

Job的class多了2個annotation，
org.quartz.PersistJobDataAfterExecution
跟
org.quartz.DisallowConcurrentExecution


DisallowConcurrentExecution
一次只能有一個job instance執行，不能多個。
比如說A job，執行需要34秒，且它被排定每30秒執行一次，這種情況就會有一段時間有2個A job instance在執行。

PersistJobDataAfterExecution
會讓JobDataMap的資料，在job執行完之後，才存入scheduler的JobStore裡。

Note: to prevent race-conditions on saved data.


Quartz每次都會new一個新的job instance，
所以不能用class member variable來保持狀態。


====================================================================================================
example5
This example is designed to demonstrate concepts related to trigger misfires.
不同的misfire策略。

Note: The trigger for job #2 is set with a misfire instruction
that will cause it to reschedule with the existing repeat count.
This policy forces quartz to refire the trigger as soon as possible.

Job #1 uses the default “smart” misfire policy for simple triggers,
which causes the trigger to fire at it’s next normal execution time.

====================================================================================================
Example 6 - Dealing with Job Exceptions

Job發生exception後繼續跑。

Job發生exception後停止相關trigger，不繼續該job。


====================================================================================================
Example 7 - Interrupting Jobs
Shows how the scheduler can interrupt your jobs and how to code your jobs to deal with interruptions

實作InterruptableJob interface。
org.quartz.InterruptableJob.interrupt()

用來停止job?




====================================================================================================
Example 8 - Fun with Calendars
Demonstrates how a Holiday calendar can be used to exclude execution of jobs on a holiday

排除某幾天，不執行job。

====================================================================================================
Example 9 - Job Listeners
Use job listeners to have one job trigger another job, building a simple workflow




有一個JobChainingJobListener，
可以用來串Job，但是job會是已經加入shceduler的樣子。
job要是durable。

    JobDetail job = newJob(SimpleJob1.class).withIdentity("job1").build();
    JobDetail job2 = newJob(SimpleJob2.class).withIdentity("job2").storeDurably().build();
    sched.addJob(job2, true);
    jobChainingJobListener.addJobChainLink(job.getKey(), job2.getKey());
    sched.getListenerManager().addJobListener(jobChainingJobListener);

前一個發生exception錯誤，下一個還是會照跑。
JobListener的jobWasExecuted(JobExecutionContext, JobExecutionException) method，
有一個JobExecutionException參數，
試過，前一個job發生exception時，要執行下一個job，會進到jobWasExecuted(JobExecutionContext, JobExecutionException)這個method，
且JobExecutionException參數不會是null，前一個正常結束，則這個參數會是null。
也許就用這個判斷，前一個是否成功執行完成。


另一個問題，會不會前一個還在跑，就執行第2個了???
測試是一個接一個。

====================================================================================================
Example 10 - Using Quartz Plug-Ins
Demonstrates the use of the XML Job Initialization plug-in as well as the History Logging plug-ins

這個有設定檔。

====================================================================================================
Example 11 - Quartz Under High Load
Quartz can run a lot of jobs but see how thread pools can limit how many jobs can execute simultaneously

這個有設定檔。

====================================================================================================
Example 12 - Remote Job Scheduling using RMI
Using Remote Method Invocation, a Quartz scheduler can be remotely scheduled by a client

這個有設定檔。

一種Client-Server的架構。
透過設定，Server可以接收Client送來的trigger跟job。
Server端負責執行，Client端可以排新的trigger跟job，但不會執行job。


====================================================================================================
Example 13 - Clustered Quartz
Demonstrates how Quartz can be used in a clustered environment and how Quartz can use the database to persist scheduling information
這個有設定檔。


