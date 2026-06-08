package io.nowcrypto.library.data.di

import io.nowcrypto.library.data.ApiConstants.API_END_POINT
import io.nowcrypto.library.data.di.interceptor.AuthInterceptor
import io.nowcrypto.library.data.di.interceptor.TokenAuthenticator
import io.nowcrypto.library.data.session.SessionManager
import io.nowcrypto.library.remote.add_fund.AddFundApi
import io.nowcrypto.library.remote.currency.CurrencyApi
import io.nowcrypto.library.remote.device_id.DeviceApi
import io.nowcrypto.library.remote.login.LoginApi
import io.nowcrypto.library.remote.payment.PaymentApi
import io.nowcrypto.library.remote.payment.TransactionIdPaymentApi
import io.nowcrypto.library.remote.payment_request_token.PaymentRequestTokenApi
import io.nowcrypto.library.remote.payment_request_token.SubRequestTokenApi
import io.nowcrypto.library.remote.payment_status.PaymentStatusApi
import io.nowcrypto.library.remote.register.RegisterApi
import io.nowcrypto.library.remote.subscription_list.SubscriptionListApi
import io.nowcrypto.library.remote.balance.BalanceApi
import io.nowcrypto.library.remote.confirm_block.ConfirmBlockApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(sessionManager: SessionManager): AuthInterceptor =
        AuthInterceptor(sessionManager)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(authInterceptor) // attach token interceptor
            .authenticator(tokenAuthenticator)
            .build()

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(API_END_POINT)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideDeviceApi(retrofit: Retrofit): DeviceApi =
        retrofit.create(DeviceApi::class.java)

    @Provides
    @Singleton
    fun provideWalletApi(retrofit: Retrofit): BalanceApi =
        retrofit.create(BalanceApi::class.java)

    @Provides
    @Singleton
    fun providePaymentApi(retrofit: Retrofit): PaymentApi =
        retrofit.create(PaymentApi::class.java)

    @Provides
    @Singleton
    fun provideConfirmBlockApi(retrofit: Retrofit): ConfirmBlockApi =
        retrofit.create(ConfirmBlockApi::class.java)

    @Provides
    @Singleton
    fun provideTransactionIdPaymentApi(retrofit: Retrofit): TransactionIdPaymentApi =
        retrofit.create(TransactionIdPaymentApi::class.java)

    @Provides
    @Singleton
    fun provideRegisterApi(retrofit: Retrofit): RegisterApi =
        retrofit.create(RegisterApi::class.java)

    @Provides
    @Singleton
    fun provideLoginApi(retrofit: Retrofit): LoginApi =
        retrofit.create(LoginApi::class.java)

    @Provides
    @Singleton
    fun providePaymentStatusApi(retrofit: Retrofit): PaymentStatusApi =
        retrofit.create(PaymentStatusApi::class.java)

    @Provides
    @Singleton
    fun provideSubscriptionListApi(retrofit: Retrofit): SubscriptionListApi =
        retrofit.create(SubscriptionListApi::class.java)

    @Provides
    @Singleton
    fun providePaymentRequestTokenApi(retrofit: Retrofit): PaymentRequestTokenApi =
        retrofit.create(PaymentRequestTokenApi::class.java)

    @Provides
    @Singleton
    fun provideSubRequestTokenApi(retrofit: Retrofit): SubRequestTokenApi =
        retrofit.create(SubRequestTokenApi::class.java)

    @Provides
    @Singleton
    fun provideCurrencyApi(retrofit: Retrofit): CurrencyApi =
        retrofit.create(CurrencyApi::class.java)

    @Provides
    @Singleton
    fun provideAddFundApi(retrofit: Retrofit): AddFundApi =
        retrofit.create(AddFundApi::class.java)
}
