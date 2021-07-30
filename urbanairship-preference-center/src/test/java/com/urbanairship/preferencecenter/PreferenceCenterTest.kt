package com.urbanairship.preferencecenter

import android.content.Context
import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.urbanairship.PreferenceDataStore
import com.urbanairship.PrivacyManager
import com.urbanairship.TestApplication
import com.urbanairship.job.JobDispatcher
import com.urbanairship.json.JsonMap
import com.urbanairship.preferencecenter.PreferenceCenter.Companion.KEY_PREFERENCE_FORMS
import com.urbanairship.preferencecenter.PreferenceCenter.Companion.PAYLOAD_TYPE
import com.urbanairship.preferencecenter.data.CommonDisplay
import com.urbanairship.preferencecenter.data.PreferenceCenterConfig
import com.urbanairship.preferencecenter.util.jsonListOf
import com.urbanairship.preferencecenter.util.jsonMapOf
import com.urbanairship.reactive.Observable
import com.urbanairship.reactive.Subject
import com.urbanairship.remotedata.RemoteData
import com.urbanairship.remotedata.RemoteDataPayload
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class PreferenceCenterTest {

    companion object {
        private val EMPTY_PAYLOADS = RemoteDataPayload.newBuilder()
                .setType(PAYLOAD_TYPE)
                .setTimeStamp(0L)
                .setMetadata(JsonMap.EMPTY_MAP)
                .setData(JsonMap.EMPTY_MAP)
                .build()

        private const val ID_1 = "id-1"
        private const val ID_2 = "id-2"

        private val PREFERENCE_FORM_1 = PreferenceCenterConfig(ID_1, emptyList(), CommonDisplay.EMPTY)
        private val PREFERENCE_FORM_2 = PreferenceCenterConfig(ID_2, emptyList(), CommonDisplay.EMPTY)

        private val METADATA = jsonMapOf("metadata" to "foo")

        private val SINGLE_FORM_PAYLOAD = RemoteDataPayload.newBuilder()
                .setType(PAYLOAD_TYPE)
                .setTimeStamp(1L)
                .setMetadata(METADATA)
                .setData(jsonMapOf(KEY_PREFERENCE_FORMS to jsonListOf(PREFERENCE_FORM_1.toJson())))
                .build()

        private val MULTI_FORM_PAYLOAD = RemoteDataPayload.newBuilder()
                .setType(PAYLOAD_TYPE)
                .setTimeStamp(2L)
                .setMetadata(METADATA)
                .setData(jsonMapOf(KEY_PREFERENCE_FORMS to jsonListOf(PREFERENCE_FORM_1.toJson(), PREFERENCE_FORM_2.toJson())))
                .build()
    }

    private val context: Context = TestApplication.getApplication()
    private val dataStore = PreferenceDataStore.inMemoryStore(context)
    private val privacyManager = PrivacyManager(dataStore, PrivacyManager.FEATURE_ALL)

    private val payloads = Subject.create<RemoteDataPayload>()
    private val remoteData: RemoteData = mock {
        on { payloadsForType(eq(PAYLOAD_TYPE)) } doReturn payloads
    }

    private val jobDispatcher: JobDispatcher = mock()
    private val backgroundLooper: Looper = Looper.getMainLooper()
    private val onOpenListener: PreferenceCenter.OnOpenListener = mock()

    private lateinit var prefCenter: PreferenceCenter

    @Before
    fun setUp() {
        prefCenter = PreferenceCenter(context, dataStore, privacyManager, remoteData, backgroundLooper, jobDispatcher)
    }

    @Test
    fun testOnOpenListener() {
        whenever(remoteData.payloadsForType(eq(PAYLOAD_TYPE)))
            .doReturn(Observable.just(SINGLE_FORM_PAYLOAD))

        prefCenter.openListener = onOpenListener
        verify(onOpenListener, never()).onOpenPreferenceCenter(any())

        prefCenter.open(ID_1)
        verify(onOpenListener).onOpenPreferenceCenter(eq(ID_1))
    }

    @Test
    fun testGetConfigWithEmptyRemoteDataPayload() {
        whenever(remoteData.payloadsForType(eq(PAYLOAD_TYPE)))
            .doReturn(Observable.just(EMPTY_PAYLOADS))

        val pendingResult = prefCenter.getConfig(ID_1)
        assertEquals(null, pendingResult.result)
    }

    @Test
    fun testGetConfigWithSingleRemoteDataPayload() {
        whenever(remoteData.payloadsForType(eq(PAYLOAD_TYPE)))
            .doReturn(Observable.just(SINGLE_FORM_PAYLOAD))

        val pendingResult = prefCenter.getConfig(ID_1)
        assertEquals(PREFERENCE_FORM_1, pendingResult.result)
    }

    @Test
    fun testGetConfigWithMultipleRemoteDataPayloads() {
        whenever(remoteData.payloadsForType(eq(PAYLOAD_TYPE)))
            .doReturn(Observable.just(MULTI_FORM_PAYLOAD))

        val pendingResult1 = prefCenter.getConfig(ID_1)
        assertEquals(PREFERENCE_FORM_1, pendingResult1.result)

        val pendingResult2 = prefCenter.getConfig(ID_2)
        assertEquals(PREFERENCE_FORM_2, pendingResult2.result)
    }

    @Test
    fun testGetConfigWithRemoteDataError() {
        whenever(remoteData.payloadsForType(eq(PAYLOAD_TYPE)))
            .doReturn(Observable.error(IllegalStateException("oops")))

        val pendingResult = prefCenter.getConfig("foo")
        assertEquals(null, pendingResult.result)
    }
}