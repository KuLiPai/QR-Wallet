package com.kulipai.qrwallet.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import androidx.core.content.edit

@Singleton
class CardRepository @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("card_storage", Context.MODE_PRIVATE)

    private val KEY_CARDS = "cards"

    private fun fromJson(obj: JSONObject): CardInfo {
        return CardInfo(
            id = obj.optString("id"),
            title = obj.optString("title", null),
            description = obj.optString("description", null),
            content = obj.optString("content", null),
            color = obj.optString("color", null)
        )
    }

    private fun toJson(card: CardInfo): JSONObject {
        return JSONObject().apply {
            put("id", card.id)
            card.title?.let { put("title", it) }
            card.description?.let { put("description", it) }
            card.content?.let { put("content", it) }
            card.color?.let { put("color", it) }
        }
    }

    fun getAll(): List<CardInfo> {
        val json = prefs.getString(KEY_CARDS, "[]") ?: "[]"
        val arr = JSONArray(json)
        return buildList {
            for (i in 0 until arr.length()) {
                val obj = arr.optJSONObject(i) ?: continue
                add(fromJson(obj))
            }
        }
    }

    fun add(card: CardInfo): CardInfo {
        val newCard = if (card.id.isBlank()) {
            card.copy(id = UUID.randomUUID().toString())
        } else card
        val list = getAll().toMutableList()
        list.add(newCard)
        save(list)
        return newCard
    }

    fun update(card: CardInfo) {
        val list = getAll().toMutableList()
        val index = list.indexOfFirst { it.id == card.id }
        if (index != -1) {
            list[index] = card
            save(list)
        }
    }

    fun delete(id: String) {
        val list = getAll().filterNot { it.id == id }
        save(list)
    }

    private fun save(list: List<CardInfo>) {
        val arr = JSONArray()
        list.forEach { arr.put(toJson(it)) }
        prefs.edit { putString(KEY_CARDS, arr.toString()) }
    }
}
