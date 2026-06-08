package io.nowcrypto.library.data.di

import android.content.Context
import io.nowcrypto.library.data.di.add_fund.AddFundRepository
import io.nowcrypto.library.data.di.add_fund.AddFundRepositoryImpl
import io.nowcrypto.library.data.di.currency.CurrencyRepository
import io.nowcrypto.library.data.di.currency.CurrencyRepositoryImpl
import io.nowcrypto.library.data.di.device_id.AndroidDeviceIdProvider
import io.nowcrypto.library.data.di.device_id.DeviceRepository
import io.nowcrypto.library.data.di.device_id.DeviceRepositoryImpl
import io.nowcrypto.library.data.di.login.LoginRepository
import io.nowcrypto.library.data.di.login.LoginRepositoryImpl
import io.nowcrypto.library.data.di.payment.PaymentRepository
import io.nowcrypto.library.data.di.payment.PaymentRepositoryImpl
import io.nowcrypto.library.data.di.payment_request_token.PaymentRequestTokenRepository
import io.nowcrypto.library.data.di.payment_request_token.PaymentRequestTokenRepositoryImpl
import io.nowcrypto.library.data.di.payment_request_token.SubRequestTokenRepository
import io.nowcrypto.library.data.di.payment_request_token.SubRequestTokenRepositoryImpl
import io.nowcrypto.library.data.di.payment_status.PaymentStatusRepository
import io.nowcrypto.library.data.di.payment_status.PaymentStatusRepositoryImpl
import io.nowcrypto.library.data.di.register.RegisterRepository
import io.nowcrypto.library.data.di.register.RegisterRepositoryImpl
import io.nowcrypto.library.data.di.subscription_list.SubscriptionListRepository
import io.nowcrypto.library.data.di.subscription_list.SubscriptionListRepositoryImpl
import io.nowcrypto.library.data.di.balance.BalanceRepository
import io.nowcrypto.library.data.di.balance.BalanceRepositoryImpl
import io.nowcrypto.library.data.di.confirm_block.ConfirmBlockRepository
import io.nowcrypto.library.data.di.confirm_block.ConfirmBlockRepositoryImpl
import io.nowcrypto.library.domain.add_fund.AddFundUseCase
import io.nowcrypto.library.domain.currency.CurrencyUseCase
import io.nowcrypto.library.domain.device_id.DeviceIdProvider
import io.nowcrypto.library.domain.device_id.GetUserDetailsUseCase
import io.nowcrypto.library.domain.device_id.SendDeviceIdUseCase
import io.nowcrypto.library.domain.login.LoginUseCase
import io.nowcrypto.library.domain.payment.PayViaCryptoUseCase
import io.nowcrypto.library.domain.payment.PayViaTransactionIdUseCase
import io.nowcrypto.library.domain.payment_request_token.PaymentRequestTokenUseCase
import io.nowcrypto.library.domain.payment_request_token.SubRequestTokenUseCase
import io.nowcrypto.library.domain.payment_status.PaymentStatusUseCase
import io.nowcrypto.library.domain.register.RegisterUseCase
import io.nowcrypto.library.domain.subscription_list.SubscriptionListUseCase
import io.nowcrypto.library.domain.balance.FetchUserBalanceUseCase
import io.nowcrypto.library.domain.confirm_block.ConfirmBlockUseCase
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides @Singleton
    fun provideDeviceRepository(api: DeviceApi): DeviceRepository =
        DeviceRepositoryImpl(api)

    @Provides @Singleton
    fun provideWalletRepository(api: BalanceApi): BalanceRepository =
        BalanceRepositoryImpl(api)

    @Provides @Singleton
    fun providePaymentRepository(paymentApi: PaymentApi, transactionIdPaymentApi: TransactionIdPaymentApi): PaymentRepository =
        PaymentRepositoryImpl(paymentApi, transactionIdPaymentApi)

    @Provides @Singleton
    fun provideSubRepository(api: SubRequestTokenApi): SubRequestTokenRepository =
        SubRequestTokenRepositoryImpl(api)

    @Provides @Singleton
    fun provideRegisterRepository(api: RegisterApi): RegisterRepository =
        RegisterRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideDeviceIdProvider(
        @ApplicationContext context: Context
    ): DeviceIdProvider =
        AndroidDeviceIdProvider(context)

    @Provides @Singleton
    fun provideLoginRepository(api: LoginApi): LoginRepository =
        LoginRepositoryImpl(api)

    @Provides @Singleton
    fun providePaymentStatusRepository(api: PaymentStatusApi): PaymentStatusRepository =
        PaymentStatusRepositoryImpl(api)

    @Provides @Singleton
    fun provideSubscriptionListRepository(api: SubscriptionListApi): SubscriptionListRepository =
        SubscriptionListRepositoryImpl(api)

    @Provides @Singleton
    fun providePaymentRequestTokenRepository(api: PaymentRequestTokenApi): PaymentRequestTokenRepository =
        PaymentRequestTokenRepositoryImpl(api)

    @Provides @Singleton
    fun provideCurrencyRepository(api: CurrencyApi): CurrencyRepository =
        CurrencyRepositoryImpl(api)

    @Provides @Singleton
    fun provideAddFundRepository(api: AddFundApi): AddFundRepository =
        AddFundRepositoryImpl(api)

    @Provides @Singleton
    fun provideConfirmBlockRepository(api: ConfirmBlockApi): ConfirmBlockRepository =
        ConfirmBlockRepositoryImpl(api)

    @Provides @Singleton
    fun provideSendDeviceIdUseCase(repository: DeviceRepository): SendDeviceIdUseCase =
        SendDeviceIdUseCase(repository)

    @Provides @Singleton
    fun provideGetUserDetailsUseCase(repository: DeviceRepository): GetUserDetailsUseCase =
        GetUserDetailsUseCase(repository)

    @Provides @Singleton
    fun provideFetchUserWalletsUseCase(repository: BalanceRepository): FetchUserBalanceUseCase =
        FetchUserBalanceUseCase(repository)

    @Provides @Singleton
    fun providePayViaCryptoUseCase(repository: PaymentRepository): PayViaCryptoUseCase =
        PayViaCryptoUseCase(repository)

    @Provides @Singleton
    fun providePayViaTransactionIdUseCase(repository: PaymentRepository): PayViaTransactionIdUseCase =
        PayViaTransactionIdUseCase(repository)

    @Provides @Singleton
    fun provideRegisterUseCase(repository: RegisterRepository): RegisterUseCase =
        RegisterUseCase(repository)

    @Provides @Singleton
    fun provideLoginUseCase(repository: LoginRepository): LoginUseCase =
        LoginUseCase(repository)

    @Provides @Singleton
    fun providePaymentStatusUseCase(repository: PaymentStatusRepository): PaymentStatusUseCase =
        PaymentStatusUseCase(repository)

    @Provides @Singleton
    fun provideSubscriptionListUseCase(repository: SubscriptionListRepository): SubscriptionListUseCase =
        SubscriptionListUseCase(repository)

    @Provides @Singleton
    fun providePaymentRequestTokenUseCase(repository: PaymentRequestTokenRepository): PaymentRequestTokenUseCase =
        PaymentRequestTokenUseCase(repository)

    @Provides @Singleton
    fun provideSubRequestTokenUseCase(repository: SubRequestTokenRepository): SubRequestTokenUseCase =
        SubRequestTokenUseCase(repository)

    @Provides @Singleton
    fun provideCurrencyUseCase(repository: CurrencyRepository): CurrencyUseCase =
        CurrencyUseCase(repository)

    @Provides @Singleton
    fun provideAddFundUseCase(repository: AddFundRepository): AddFundUseCase =
        AddFundUseCase(repository)

    @Provides @Singleton
    fun provideConfirmBlockUseCase(repository: ConfirmBlockRepository): ConfirmBlockUseCase =
        ConfirmBlockUseCase(repository)
}

