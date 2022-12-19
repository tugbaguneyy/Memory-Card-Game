package com.example.memorygame.models

import com.example.memorygame.R
import com.example.memorygame.utils.DEFAULT_ICONS1
import com.example.memorygame.utils.DEFAULT_ICONS2
import com.example.memorygame.utils.DEFAULT_ICONS3
import com.example.memorygame.utils.DEFAULT_ICONS4

class MemoryGame(private val boardSize: BoardSize){


    lateinit var cards: List<MemoryCard>
    var numPairsFound =0

    private var numCardFlips= 0
    private var indexOfSingleSelectedCard: Int? = null

    init {
        if(boardSize.getNumPairs()==2){
            val chosenImages1 = DEFAULT_ICONS1.shuffled().take(boardSize.getNumPairs()/2)
            val chosenImages3 = DEFAULT_ICONS3.shuffled().take(boardSize.getNumPairs()/2)
            val randomizedImages = (chosenImages3 + chosenImages3+chosenImages1+chosenImages1).shuffled()
            cards= randomizedImages.map { MemoryCard(it) }
        }
        if(boardSize.getNumPairs()==8){
            val chosenImages1 = DEFAULT_ICONS1.shuffled().take(boardSize.getNumPairs()/4)
            val chosenImages2 = DEFAULT_ICONS2.shuffled().take(boardSize.getNumPairs()/4)
            val chosenImages3 = DEFAULT_ICONS3.shuffled().take(boardSize.getNumPairs()/4)
            val chosenImages4 = DEFAULT_ICONS4.shuffled().take(boardSize.getNumPairs()/4)
            val randomizedImages = (chosenImages2 + chosenImages2+chosenImages1+chosenImages1+chosenImages3+chosenImages3+chosenImages4+chosenImages4).shuffled()
            cards= randomizedImages.map { MemoryCard(it) }
        }
        if(boardSize.getNumPairs()==18){
            val chosenImages1 = DEFAULT_ICONS1.shuffled().take(boardSize.getNumPairs()/6)
            val chosenImages2 = DEFAULT_ICONS2.shuffled().take(boardSize.getNumPairs()/6)
            val chosenImages3 = DEFAULT_ICONS3.shuffled().take(boardSize.getNumPairs()/3)
            val chosenImages4 = DEFAULT_ICONS4.shuffled().take(boardSize.getNumPairs()/3)
            val randomizedImages = (chosenImages2 + chosenImages2+chosenImages1+chosenImages1+chosenImages3+chosenImages3+chosenImages4+chosenImages4).shuffled()
            cards= randomizedImages.map { MemoryCard(it) }
        }
    }

    fun flipCard(position: Int): Boolean{

        numCardFlips++
       val card= cards[position]
        var foundMatch = false
        if (indexOfSingleSelectedCard == null){
            restoreCards()
            indexOfSingleSelectedCard = position
        }else{
            foundMatch= checkForMatch(indexOfSingleSelectedCard!!,position)
            indexOfSingleSelectedCard= null
        }
        card.isFaceUp= !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if(cards[position1].identifier != cards[position2].identifier){
            return false
        }
        cards[position1].isMatched=true
        cards[position2].isMatched=true
        numPairsFound++
        //puan
        return true
    }

    private fun restoreCards() {
        for(card in cards){
            if(!card.isMatched){
            card.isFaceUp=false
                //puanadus
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound==boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips / 2
    }
}