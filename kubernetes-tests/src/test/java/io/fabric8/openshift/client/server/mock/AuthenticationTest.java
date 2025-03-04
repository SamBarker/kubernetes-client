/*
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.openshift.client.server.mock;

import io.fabric8.kubernetes.client.server.mock.EnableKubernetesMockClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesMockServer;
import io.fabric8.openshift.api.model.config.v1.Authentication;
import io.fabric8.openshift.api.model.config.v1.AuthenticationBuilder;
import io.fabric8.openshift.api.model.config.v1.AuthenticationList;
import io.fabric8.openshift.api.model.config.v1.AuthenticationListBuilder;
import io.fabric8.openshift.client.OpenShiftClient;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;

import static org.assertj.core.api.Assertions.assertThat;

@EnableKubernetesMockClient
class AuthenticationTest {
  private OpenShiftClient client;
  KubernetesMockServer server;

  @Test
  void get() {
    // Given
    server.expect().get().withPath("/apis/config.openshift.io/v1/authentications/test-get")
        .andReturn(HttpURLConnection.HTTP_OK, createNewAuthentication("test-get"))
        .once();

    // When
    Authentication authentication = client.config().authentications().withName("test-get").get();

    // Then
    assertThat(authentication)
        .isNotNull()
        .hasFieldOrPropertyWithValue("metadata.name", "test-get");
  }

  @Test
  void list() {
    // Given
    server.expect().get().withPath("/apis/config.openshift.io/v1/authentications")
        .andReturn(HttpURLConnection.HTTP_OK, new AuthenticationListBuilder()
            .addToItems(createNewAuthentication("test-list"))
            .build())
        .once();

    // When
    AuthenticationList authenticationList = client.config().authentications().list();

    // Then
    assertThat(authenticationList).isNotNull();
    assertThat(authenticationList.getItems()).hasSize(1);
    assertThat(authenticationList.getItems().get(0))
        .hasFieldOrPropertyWithValue("metadata.name", "test-list");
  }

  @Test
  void delete() {
    // Given
    server.expect().delete().withPath("/apis/config.openshift.io/v1/authentications/cluster")
        .andReturn(HttpURLConnection.HTTP_OK, createNewAuthentication("cluster"))
        .once();

    // When
    boolean isDeleted = client.config().authentications().withName("cluster").delete().size() == 1;

    // Then
    assertThat(isDeleted).isTrue();
  }

  private Authentication createNewAuthentication(String name) {
    return new AuthenticationBuilder()
        .withNewMetadata().withName(name).endMetadata()
        .withNewSpec()
        .withNewOauthMetadata()
        .withName("foo")
        .endOauthMetadata()
        .endSpec()
        .build();
  }
}
