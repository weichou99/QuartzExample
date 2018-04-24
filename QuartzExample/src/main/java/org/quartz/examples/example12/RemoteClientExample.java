/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 */

package org.quartz.examples.example12;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerListener;
import org.quartz.SchedulerMetaData;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example is a client program that will remotely
 * talk to the scheduler to schedule a job.   In this
 * example, we will need to use the JDBC Job Store.  The
 * client will connect to the JDBC Job Store remotely to
 * schedule the job.
 *
 * @author James House, Bill Kratzer
 */
public class RemoteClientExample {

    public void run() throws Exception {

        Logger log = LoggerFactory.getLogger(RemoteClientExample.class);

        // First we must get a reference to a scheduler
        SchedulerFactory sf = new StdSchedulerFactory("example12/server.properties");
        Scheduler sched = sf.getScheduler();



        // define the job and ask it to run
        JobDetail job = newJob(SimpleJob.class)
            .withIdentity("remotelyAddedJob", "default")
            .build();

        JobDataMap map = job.getJobDataMap();
        map.put("msg", "Your remotely added job has executed!");

        Trigger trigger = newTrigger()
            .withIdentity("remotelyAddedTrigger", "default")
            .forJob(job.getKey())
            .withSchedule(cronSchedule("/5 * * ? * *"))
            .build();

        // schedule the job
        sched.scheduleJob(job, trigger);
        try {
            Thread.sleep(6L * 1000L);
          } catch (Exception e) {
            //
          }


        SchedulerMetaData metaData = sched.getMetaData();
        int numberOfJobsExecuted = metaData.getNumberOfJobsExecuted();
        System.out.format("numberOfJobsExecuted:[%s]%n", numberOfJobsExecuted);
        String summary = metaData.getSummary();
        System.out.format("summary:[%s]%n", summary);
        Class<?> jobStoreClass = metaData.getJobStoreClass();
        System.out.format("jobStoreClass:[%s]%n", jobStoreClass);
        System.out.format("sched:[%s]%n", sched);
        SchedulerContext context = sched.getContext();
        System.out.format("context:[%s]%n", context);
        Collection<Object> values = context.values();
        System.out.format("values:[%s]%n", values);
        Set<Entry<String,Object>> entrySet = context.entrySet();
        System.out.format("entrySet:[%s]%n", entrySet.size());
        for (Entry<String, Object> entry : entrySet) {
			System.out.format("entry:[%s]%n", entry);

		}

        Map<String, Object> wrappedMap = context.getWrappedMap();
        System.out.format("wrappedMap:[%s]%n", wrappedMap);

        SchedulerListener schedulerListener = null;
		sched.getListenerManager().addSchedulerListener(schedulerListener);
        log.info("Remote job scheduled.");
    }

    public static void main(String[] args) throws Exception {

        RemoteClientExample example = new RemoteClientExample();
        example.run();
    }

}
