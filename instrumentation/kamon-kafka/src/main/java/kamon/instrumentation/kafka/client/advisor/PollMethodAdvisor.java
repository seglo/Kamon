/* =========================================================================================
 * Copyright (C) 2013-2020 the kamon project <http://kamon.io/>
 *
 * Licensed under the Apache License, Version 2.0 (the "License") you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 * =========================================================================================
 */
package kamon.instrumentation.kafka.client.advisor;

import kamon.Kamon;
import kamon.instrumentation.kafka.client.RecordProcessor;
import kanela.agent.libs.net.bytebuddy.asm.Advice;
import kanela.agent.libs.net.bytebuddy.implementation.bytecode.assign.Assigner;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Instant;

/**
 * Consumer Instrumentation
 */
public class PollMethodAdvisor {
    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static void onEnter(@Advice.Local("startTime") Instant startTime) {
        startTime = Kamon.clock().instant();
    }

    @Advice.OnMethodExit(suppress = Throwable.class)
    public static <K, V> void onExit(
            @Advice.Local("startTime") Instant startTime,
            @Advice.FieldValue(value = "groupId", typing = Assigner.Typing.DYNAMIC) Object groupId,
            @Advice.FieldValue("clientId") String clientId,
            @Advice.Return(readOnly = false) ConsumerRecords<K, V> records) {

        records = RecordProcessor.process(startTime, clientId, groupId, records);
    }
}