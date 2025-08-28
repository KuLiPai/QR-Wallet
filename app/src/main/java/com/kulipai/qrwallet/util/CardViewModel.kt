package com.kulipai.qrwallet.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kulipai.qrwallet.data.CardInfo
import com.kulipai.qrwallet.data.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel
@Inject constructor(
    private val application: Application, // Hilt 可以注入 Application Context
    private val repository: CardRepository
) : ViewModel() {

    private val _cards = MutableStateFlow<List<CardInfo>>(emptyList())
    val cards: StateFlow<List<CardInfo>> = _cards

    init {
        loadCards()
    }

    private fun loadCards() {
        _cards.value = repository.getAll()
    }

    fun addCard(card: CardInfo) {
        viewModelScope.launch {
            repository.add(card)
            loadCards()
        }
    }

    fun updateCard(card: CardInfo) {
        viewModelScope.launch {
            repository.update(card)
            loadCards()
        }
    }

    fun deleteCard(id: String) {
        viewModelScope.launch {
            repository.delete(id)
            loadCards()
        }
    }
}