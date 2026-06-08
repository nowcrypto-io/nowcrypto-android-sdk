package io.nowcrypto.library.data.di.device_id

import android.content.Context
import android.provider.Settings
import io.nowcrypto.library.domain.device_id.DeviceIdProvider

class AndroidDeviceIdProvider(private val context: Context) : DeviceIdProvider {
    override fun getDeviceId(): String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
}