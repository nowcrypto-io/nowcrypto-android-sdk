package io.nowcrypto.library.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.nowcrypto.library.data.ApiConstants.API_END_POINT
import io.nowcrypto.library.data.di.add_fund.AddFundRepositoryImpl
import io.nowcrypto.library.data.di.balance.BalanceRepositoryImpl
import io.nowcrypto.library.data.di.confirm_block.ConfirmBlockRepositoryImpl
import io.nowcrypto.library.data.di.currency.CurrencyRepositoryImpl
import io.nowcrypto.library.data.di.device_id.AndroidDeviceIdProvider
import io.nowcrypto.library.data.di.device_id.DeviceRepositoryImpl
import io.nowcrypto.library.data.di.interceptor.AuthInterceptor
import io.nowcrypto.library.data.di.interceptor.TokenAuthenticator
import io.nowcrypto.library.data.di.login.LoginRepositoryImpl
import io.nowcrypto.library.data.di.payment.PaymentRepositoryImpl
import io.nowcrypto.library.data.di.payment_request_token.PaymentRequestTokenRepositoryImpl
import io.nowcrypto.library.data.di.payment_request_token.SubRequestTokenRepositoryImpl
import io.nowcrypto.library.data.di.payment_status.PaymentStatusRepositoryImpl
import io.nowcrypto.library.data.di.register.RegisterRepositoryImpl
import io.nowcrypto.library.data.di.subscription.QueryActiveSubscriptionRepositoryImpl
import io.nowcrypto.library.data.di.subscription.SubscriptionListRepositoryImpl
import io.nowcrypto.library.data.session.SessionManager
import io.nowcrypto.library.domain.add_fund.AddFundUseCase
import io.nowcrypto.library.domain.balance.FetchUserBalanceUseCase
import io.nowcrypto.library.domain.confirm_block.ConfirmBlockUseCase
import io.nowcrypto.library.domain.currency.CurrencyUseCase
import io.nowcrypto.library.domain.device_id.GetUserDetailsUseCase
import io.nowcrypto.library.domain.device_id.SendDeviceIdUseCase
import io.nowcrypto.library.domain.login.LoginUseCase
import io.nowcrypto.library.domain.payment.PayViaCryptoUseCase
import io.nowcrypto.library.domain.payment.PayViaTransactionIdUseCase
import io.nowcrypto.library.domain.payment_request_token.PaymentRequestTokenUseCase
import io.nowcrypto.library.domain.payment_request_token.SubRequestTokenUseCase
import io.nowcrypto.library.domain.payment_status.PaymentStatusUseCase
import io.nowcrypto.library.domain.register.RegisterUseCase
import io.nowcrypto.library.domain.subscription.QueryActiveSubscriptionUseCase
import io.nowcrypto.library.domain.subscription.SubscriptionListUseCase
import io.nowcrypto.library.remote.add_fund.AddFundApi
import io.nowcrypto.library.remote.balance.BalanceApi
import io.nowcrypto.library.remote.confirm_block.ConfirmBlockApi
import io.nowcrypto.library.remote.currency.CurrencyApi
import io.nowcrypto.library.remote.device_id.DeviceApi
import io.nowcrypto.library.remote.login.LoginApi
import io.nowcrypto.library.remote.payment.PaymentApi
import io.nowcrypto.library.remote.payment.TransactionIdPaymentApi
import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenApi
import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenApi
import io.nowcrypto.library.remote.payment_status.PaymentStatusApi
import io.nowcrypto.library.remote.register.RegisterApi
import io.nowcrypto.library.remote.subscription.QueryActiveSubscriptionApi
import io.nowcrypto.library.remote.subscription.SubscriptionListApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

internal class NowCryptoInternal(context: Context) {

    // Storage
    val sessionManager: SessionManager by lazy {
        SessionManager(context.applicationContext.dataStore)
    }

    // Network
    private val moshi: Moshi by lazy {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(AuthInterceptor(sessionManager))
            .authenticator(TokenAuthenticator(sessionManager))
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(API_END_POINT)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // APIs
    private val deviceApi by lazy { retrofit.create(DeviceApi::class.java) }
    private val balanceApi by lazy { retrofit.create(BalanceApi::class.java) }
    private val paymentApi by lazy { retrofit.create(PaymentApi::class.java) }
    private val confirmBlockApi by lazy { retrofit.create(ConfirmBlockApi::class.java) }
    private val transactionIdPaymentApi by lazy { retrofit.create(TransactionIdPaymentApi::class.java) }
    private val registerApi by lazy { retrofit.create(RegisterApi::class.java) }
    private val loginApi by lazy { retrofit.create(LoginApi::class.java) }
    private val paymentStatusApi by lazy { retrofit.create(PaymentStatusApi::class.java) }
    private val subscriptionListApi by lazy { retrofit.create(SubscriptionListApi::class.java) }
    private val queryActiveSubscriptionApi by lazy { retrofit.create(QueryActiveSubscriptionApi::class.java) }
    private val paymentRequestTokenApi by lazy { retrofit.create(PaymentRequestTokenApi::class.java) }
    private val subRequestTokenApi by lazy { retrofit.create(SubRequestTokenApi::class.java) }
    private val currencyApi by lazy { retrofit.create(CurrencyApi::class.java) }
    private val addFundApi by lazy { retrofit.create(AddFundApi::class.java) }

    // Repositories
    private val deviceRepository by lazy { DeviceRepositoryImpl(deviceApi) }
    private val balanceRepository by lazy { BalanceRepositoryImpl(balanceApi) }
    private val paymentRepository by lazy { PaymentRepositoryImpl(paymentApi, transactionIdPaymentApi) }
    private val registerRepository by lazy { RegisterRepositoryImpl(registerApi) }
    private val loginRepository by lazy { LoginRepositoryImpl(loginApi) }
    private val paymentStatusRepository by lazy { PaymentStatusRepositoryImpl(paymentStatusApi) }
    private val subscriptionListRepository by lazy { SubscriptionListRepositoryImpl(subscriptionListApi) }
    private val queryActiveSubscriptionRepository by lazy { QueryActiveSubscriptionRepositoryImpl(queryActiveSubscriptionApi) }
    private val paymentRequestTokenRepository by lazy { PaymentRequestTokenRepositoryImpl(paymentRequestTokenApi) }
    private val subRequestTokenRepository by lazy { SubRequestTokenRepositoryImpl(subRequestTokenApi) }
    private val currencyRepository by lazy { CurrencyRepositoryImpl(currencyApi) }
    private val addFundRepository by lazy { AddFundRepositoryImpl(addFundApi) }
    private val confirmBlockRepository by lazy { ConfirmBlockRepositoryImpl(confirmBlockApi) }
    
    val deviceIdProvider by lazy { AndroidDeviceIdProvider(context.applicationContext) }

    // Use Cases
    val currencyUseCase by lazy { CurrencyUseCase(currencyRepository) }
    val paymentRequestTokenUseCase by lazy { PaymentRequestTokenUseCase(paymentRequestTokenRepository) }
    val subRequestTokenUseCase by lazy { SubRequestTokenUseCase(subRequestTokenRepository) }
    val subscriptionListUseCase by lazy { SubscriptionListUseCase(subscriptionListRepository) }
    val paymentStatusUseCase by lazy { PaymentStatusUseCase(paymentStatusRepository) }
    val queryActiveSubscriptionUseCase by lazy { QueryActiveSubscriptionUseCase(queryActiveSubscriptionRepository) }
    val sendDeviceIdUseCase by lazy { SendDeviceIdUseCase(deviceRepository) }
    val getUserDetailsUseCase by lazy { GetUserDetailsUseCase(deviceRepository) }
    val fetchUserBalanceUseCase by lazy { FetchUserBalanceUseCase(balanceRepository) }
    val payViaCryptoUseCase by lazy { PayViaCryptoUseCase(paymentRepository) }
    val payViaTransactionIdUseCase by lazy { PayViaTransactionIdUseCase(paymentRepository) }
    val registerUseCase by lazy { RegisterUseCase(registerRepository) }
    val loginUseCase by lazy { LoginUseCase(loginRepository) }
    val addFundUseCase by lazy { AddFundUseCase(addFundRepository) }
    val confirmBlockUseCase by lazy { ConfirmBlockUseCase(confirmBlockRepository) }

    companion object {
        @Volatile
        private var instance: NowCryptoInternal? = null

        fun getInstance(context: Context): NowCryptoInternal {
            return instance ?: synchronized(this) {
                instance ?: NowCryptoInternal(context).also { instance = it }
            }
        }
    }
}
