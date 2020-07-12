package com.mobile.mobilerdevfcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class NotificationService : FirebaseMessagingService() {

    companion object {
        const val PARAM_CUSTOM_MESSAGE = "PARAM_CUSTOM_MESSAGE"

        val TAG = this::class.simpleName
    }

    /**
     * FCM mesajları ve verileri ise burada yönetilmektedir.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        /**
         * Gelen bildirime payload olarak ek veriler yüklenmişse onları yönetmek ve kullanmak için
         */
        var customMessage: String? = null
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            customMessage = remoteMessage.data["customMessage"]
        }

        /**
         * Gelen bildirimin mesaj içeriğini kullanmak için
         */
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title, it.body, customMessage)
        }
    }

    /**
     * Eğer herhangi bir servisinize (API) ya da Firebase Database'e Google'ın cihaz ve
     * servis bazlı ürettiği unique token'ı göndermek
     * ve cihaz ya da kullanıcı bazlı bildirim gönderimini sağlamak isterseniz burayı kullanabilir
     * ayrıca bu metod üzerinden token değişimlerinde cihaz özelinde token'ı güncelleyebilirsiniz.
     */
    override fun onNewToken(token: String) {
        // sendRegistrationToServer(token)
    }

    /**
     * FCM mesajının ve bilgilerinin bildirimde görüntülendiği ve bildirimden gelen customMessage olarak özel bir mesajı
     * bildirime tıklayan kullanıcının sayfayı açtığında görebilmesini sağlayan metod
     *
     * @param title FCM'den gelen başlık
     * @param messageBody FCM'den gelen body mesajı
     * @param customMessage FCM'den gelen data içinde bulunan pair listesinden aldığımız "customMessage" bilgisi
     */
    private fun sendNotification(
        title: String?,
        messageBody: String?,
        customMessage: String? = null
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtras(Bundle().apply {
                putString(PARAM_CUSTOM_MESSAGE, customMessage)
            })
        }
        val pendingIntent = PendingIntent.getActivity(
            this, Random().nextInt(), intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val channelId =
            getString(R.string.mobiler_notification_channel_id) // Oluşturulan kanala bildirimi bağlamak için ortak bir ID
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_mobiler_notification) // Bildirim ikonu
            .setContentTitle(title) // Bildirim başlığı
            .setContentText(messageBody) // Bildirim mesajı
            .setSound(defaultSoundUri) // Varsayılan olarak bildirim ile beraber gelen ses
            .setContentIntent(pendingIntent) // Bildirime tıklanınca açılacak sayfa ya da tetiklenecek intent (Service, BroadcastReceiver vb.)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Oreo sürümünden itibaren "Kanal" kullanımı gereksinim haline gelmiştir.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Kanal grubunda okunabilir başlık",
                NotificationManager.IMPORTANCE_DEFAULT // Bildirimler arasında öncelik belirleme için
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0 /* Bildirime verilen ID */, notificationBuilder.build())
    }

}
