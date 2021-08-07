package dtinth.tools

import com.goterl.lazysodium.SodiumAndroid
import com.goterl.lazysodium.LazySodiumAndroid
import com.goterl.lazysodium.interfaces.Box
import com.goterl.lazysodium.utils.Key
import java.nio.charset.StandardCharsets


val sodium = SodiumAndroid()
val lazySodium: LazySodiumAndroid = LazySodiumAndroid(sodium, StandardCharsets.UTF_8)

class Sealer {
    val recipientPublicKey = Key.fromBase64String("1nh5s77GGDTURB27UG3S7gC338AI3knOOYOpDLWYTyw=")
    val box = lazySodium as Box.Lazy

    public fun seal(message: String): String {
        return box.cryptoBoxSealEasy(message, recipientPublicKey)
    }
}