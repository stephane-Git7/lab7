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

package org.gradle.api.internal.artifacts.ivyservice.resolveengine.result;

import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ComponentSelector;
import org.gradle.api.artifacts.result.DependencyResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.internal.artifacts.result.DefaultResolvedComponentResult;
import org.gradle.api.internal.artifacts.result.DefaultResolvedDependencyResult;
import org.gradle.api.internal.artifacts.result.DefaultUnresolvedDependencyResult;
import org.gradle.internal.serialize.Decoder;
import org.gradle.internal.serialize.Encoder;
import org.gradle.internal.serialize.Serializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResolvedComponentResultSerializer implements Serializer<ResolvedComponentResult> {
    private final Serializer<ModuleVersionIdentifier> moduleVersionIdSerializer;
    private final Serializer<ComponentIdentifier> componentIdSerializer;
    private final Serializer<ComponentSelector> componentSelectorSerializer;

    public ResolvedComponentResultSerializer(
        Serializer<ModuleVersionIdentifier> moduleVersionIdSerializer,
        Serializer<ComponentIdentifier> componentIdSerializer,
        Serializer<ComponentSelector> componentSelectorSerializer
    ) {
        this.moduleVersionIdSerializer = moduleVersionIdSerializer;
        this.componentIdSerializer = componentIdSerializer;
        this.componentSelectorSerializer = componentSelectorSerializer;
    }

    @Override
    public ResolvedComponentResult read(Decoder decoder) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(Encoder encoder, ResolvedComponentResult value) throws Exception {
        DefaultResolvedComponentResult root = (DefaultResolvedComponentResult) value;
        Map<ResolvedComponentResult, Integer> components = new HashMap<>();
        writeComponent(encoder, root, components);
    }

    private void writeComponent(Encoder encoder, ResolvedComponentResult component, Map<ResolvedComponentResult, Integer> components) throws Exception {
        Integer id = components.get(component);
        if (id != null) {
            // Already seen
            encoder.writeSmallInt(id);
            return;
        }
        id = components.size();
        components.put(component, id);

        encoder.writeSmallInt(id);
        moduleVersionIdSerializer.write(encoder, component.getModuleVersion());
        componentIdSerializer.write(encoder, component.getId());
        Set<? extends DependencyResult> dependencies = component.getDependencies();
        encoder.writeSmallInt(dependencies.size());
        for (DependencyResult dependency : dependencies) {
            boolean successful = dependency instanceof ResolvedDependencyResult;
            encoder.writeBoolean(successful);
            if (successful) {
                DefaultResolvedDependencyResult dependencyResult = (DefaultResolvedDependencyResult) dependency;
                componentSelectorSerializer.write(encoder, dependencyResult.getRequested());
                writeComponent(encoder, dependencyResult.getSelected(), components);
            } else {
                DefaultUnresolvedDependencyResult dependencyResult = (DefaultUnresolvedDependencyResult) dependency;
                componentSelectorSerializer.write(encoder, dependencyResult.getRequested());
            }
        }
    }
}
