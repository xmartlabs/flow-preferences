package com.tfcporciuncula.tests

import com.google.common.truth.Truth.assertThat
import com.tfcporciuncula.flow.ObjectPreference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class ObjectPreferenceTest : BaseTest() {

  private class TestObject(val id: Int)

  private val serializer =
    object : ObjectPreference.Serializer<TestObject?> {
      override fun deserialize(serialized: String?) = serialized?.let { TestObject(it.toInt()) }
      override fun serialize(value: TestObject?) = value?.id?.toString()
    }

  @Test fun testDefaultValues() {
    val preference1 = flowSharedPreferences.getObject("key", serializer, defaultValue = TestObject(0))
    assertThat(preference1.get()?.id).isEqualTo(0)

    val preference2 = flowSharedPreferences.getObject("key", serializer, defaultValue = TestObject(1))
    assertThat(preference2.get()?.id).isEqualTo(1)
  }

  @Test fun testNullDefaultValues() {
    val preference1 = flowSharedPreferences.getObject("key", serializer, defaultValue = null)
    assertThat(preference1.get()).isEqualTo(null)
  }

  @Test fun testSettingValues() {
    val preference = flowSharedPreferences.getObject("key", serializer, defaultValue = TestObject(-1))

    preference.set(TestObject(100))
    assertThat(preference.get()?.id).isEqualTo(100)

    runBlocking {
      preference.setAndCommit(TestObject(2000))
      assertThat(preference.get()?.id).isEqualTo(2000)
    }
  }

  @Test fun testNullValues() {
    val preference = flowSharedPreferences.getObject("key", serializer, defaultValue = null)

    preference.set(null)
    assertThat(preference.get()).isEqualTo(null)

    runBlocking {
      preference.setAndCommit(TestObject(2000))
      assertThat(preference.get()?.id).isEqualTo(2000)
      preference.setAndCommit(null)
      assertThat(preference.get()).isEqualTo(null)
    }
  }
}
