package com.example.delivr

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

object Repository {
    private val api: RetrofitApi by lazy {
        val client = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .build()

        Retrofit.Builder()
            .baseUrl("https://delivr1.herokuapp.com")
            //.baseUrl("http://192.168.0.101:8000")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create()
    }

    private val cart: MutableSet<ProductOrder> = HashSet()
    private var cartCost: BigDecimal? = null
    private lateinit var userPrefs: SharedPreferences
    private var isUserPrefsInitialized = false
    private lateinit var user: User

    private const val USER_PREFS_NAME = "name"
    private const val USER_PREFS_ADDRESS = "address"
    private const val USER_PREFS_TEL = "tel"
    private const val USER_PREFS_EMAIL = "email"

    fun openUserSharedPreferences(context: Context) {
        if (!isUserPrefsInitialized) {
            userPrefs =
                context.getSharedPreferences("com.example.delivr.user_prefs", Context.MODE_PRIVATE)
            isUserPrefsInitialized = true
        }
        user = loadUser()
    }

    fun getUser(): User = user

    suspend fun getCategories(): List<Category> {
        val ctg = Firebase.firestore.collection("categories").get().await()
        return ctg.map {
            Category(
                it.id,
                it["name"] as String,
                (it["image"] as String).toImageResource()
            )
        }
    }

    suspend fun getProductsForCategory(categoryId: String): List<Product> {
        return withContext(Dispatchers.IO) {
            val categoryRef = Firebase.firestore.document("/categories/$categoryId")
            val query = Firebase.firestore.collection("products").whereEqualTo("category", categoryRef)

            val result = query.get().await()
            result.map {
                Product(
                    it.id,
                    categoryId,
                    it["name"] as String,
                    (it["weight"] as Number).toInt(),
                    (it["price"] as Number).toDouble().toBigDecimal(),
                    (it["image"] as String).toImageResource()
                )
            }
        }
    }

    suspend fun addToCart(productOrder: ProductOrder) {
        cart.add(productOrder)
        cartCost = null
    }

    suspend fun removeFromCart(productOrder: ProductOrder) {
        cart.remove(productOrder)
        cartCost = null
    }

    fun getCart(): Set<ProductOrder> {
        return cart
    }

    fun getCartSize() = cart.sumOf { it.quantity }

    fun calculateCartCost(): BigDecimal {
        if (cartCost == null) {
            var sum = BigDecimal(0)
            for (product in cart) {
                sum += product.data.price.multiply(BigDecimal(product.quantity))
            }
            cartCost = sum
        }
        return cartCost!!
    }

    suspend fun makeOrder(name: String, address: String, tel: String, email: String) {
        api.makeOrder(Order(
            name,
            address,
            tel,
            email,
            cart.map {
                ProductOrderDto(it.data.name, it.data.price.toDouble(), it.quantity, it.data.image.urlRes.toString())
            }
        ))
        saveUser(name, address, tel, email)
        clearCart()
    }

    private fun loadUser(): User {
        return User(
            userPrefs.getString(USER_PREFS_NAME, "")!!,
            userPrefs.getString(USER_PREFS_ADDRESS, "")!!,
            userPrefs.getString(USER_PREFS_TEL, "")!!,
            userPrefs.getString(USER_PREFS_EMAIL, "")!!
        )
    }

    private fun saveUser(name: String, address: String, tel: String, email: String) {
        userPrefs.edit()
            .putString(USER_PREFS_NAME, name)
            .putString(USER_PREFS_ADDRESS, address)
            .putString(USER_PREFS_TEL, tel)
            .putString(USER_PREFS_EMAIL, email)
            .apply()
        user = User(name, address, tel, email)
    }

    private suspend fun clearCart() {
        cart.clear()
    }
}


suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            cont.resume(it.result!!)
        }
        addOnFailureListener {
            cont.cancel(it)
        }
    }
}

suspend fun CollectionReference.fetch(): QuerySnapshot {
    return suspendCancellableCoroutine { cont ->
        get().addOnCompleteListener {
            cont.resume(it.result!!)
        }.addOnFailureListener {
            cont.cancel(it)
        }
    }
}