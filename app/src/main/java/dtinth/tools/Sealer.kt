package dtinth.tools

import com.goterl.lazysodium.SodiumAndroid
import com.goterl.lazysodium.LazySodiumAndroid
import com.goterl.lazysodium.interfaces.Box
import com.goterl.lazysodium.utils.Key
import java.nio.charset.StandardCharsets


val sodium = SodiumAndroid()
val lazySodium: LazySodiumAndroid = LazySodiumAndroid(sodium, StandardCharsets.UTF_8)

class Sealer {
    fun seal(message: String, publicKey: String): String {
        val recipientPublicKey = Key.fromBase64String(publicKey)
        val box = lazySodium as Box.Lazy
        return box.cryptoBoxSealEasy(message, recipientPublicKey)
    }
}