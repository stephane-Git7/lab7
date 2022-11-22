/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.execution.plan;

import org.gradle.internal.service.scopes.Scopes;
import org.gradle.internal.service.scopes.ServiceScope;

@ServiceScope(Scopes.Build.class)
public class ExecutionPlanFactory {
    private final String displayName;
    private final TaskNodeFactory taskNodeFactory;
    private final TaskDependencyResolver dependencyResolver;
    private final NodeValidator nodeValidator;
    private final ExecutionNodeAccessHierarchy outputHierarchy;
    private final ExecutionNodeAccessHierarchy destroyableHierarchy;

    public ExecutionPlanFactory(
        String displayName,
        TaskNodeFactory taskNodeFactory,
        TaskDependencyResolver dependencyResolver,
        NodeValidator nodeValidator,
        ExecutionNodeAccessHierarchy outputHierarchy,
        ExecutionNodeAccessHierarchy destroyableHierarchy
    ) {
        this.displayName = displayName;
        this.taskNodeFactory = taskNodeFactory;
        this.dependencyResolver = dependencyResolver;
        this.nodeValidator = nodeValidator;
        this.outputHierarchy = outputHierarchy;
        this.destroyableHierarchy = destroyableHierarchy;
    }

    public ExecutionPlan createPlan() {
        return new DefaultExecutionPlan(displayName, taskNodeFactory, dependencyResolver, nodeValidator, outputHierarchy, destroyableHierarchy);
    }
}
