package com.example.memorygame

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.models.MemoryCard
import com.example.memorygame.models.MemoryGame
import com.example.memorygame.utils.DEFAULT_ICONS
import com.example.memorygame.utils.EXTRA_BOARD_SIZE
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG="MainActivity"
        private const val CREATE_REQUEST_CODE =248
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvHamleS: TextView
    private lateinit var tvEsS: TextView

    private lateinit var memoryGame: MemoryGame
    private lateinit var adapter: MemoryBoardAdapter
    private var boardSize: BoardSize= BoardSize.EASY

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot=findViewById(R.id.clRoot)
        rvBoard= findViewById(R.id.rvBoard)
        tvHamleS= findViewById(R.id.tvHamleS)
        tvEsS=findViewById(R.id.tvEsS)

        val intent =Intent(this,CreateActivity::class.java)
        intent.putExtra(EXTRA_BOARD_SIZE,BoardSize.EASY)
        startActivity(intent)

        setupBoard()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.refresh ->{
                if(memoryGame.getNumMoves()>0 && !memoryGame.haveWonGame()){
                    showAlertDialog("Oyun Yeniden Başlatılacak. Onaylıyor musunuz?",null,View.OnClickListener {
                       setupBoard()
                    })
                }else {
                    setupBoard()
                }
                return true
            }
            R.id.zorlukS->{
                showNewSizeDialog()
                return true
            }
            R.id.custom ->{
                showCreationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showCreationDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize= boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)

        showAlertDialog("Kendi oyununuzu oluşturun",boardSizeView,View.OnClickListener {
            val desiredBoardSize =when (radioGroupSize.checkedRadioButtonId){
                R.id.rbkolay -> BoardSize.EASY
                R.id.rborta -> BoardSize.MEDIUM
                else-> BoardSize.HARD
            }
            //navigate islemi
            val intent = Intent(this,CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE, desiredBoardSize)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size,null)
        val radioGroupSize= boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when(boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbkolay)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rborta)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbzor)
        }
        showAlertDialog("Zorluk Seviyesini Seciniz",boardSizeView,View.OnClickListener {
            boardSize =when (radioGroupSize.checkedRadioButtonId){
                R.id.rbkolay -> BoardSize.EASY
                R.id.rborta -> BoardSize.MEDIUM
                else-> BoardSize.HARD
            }
            setupBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?,positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Vazgeç",null)
            .setPositiveButton("Tamam"){ _,_->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        when(boardSize){
            BoardSize.EASY -> {
                tvHamleS.text = "Kolay: 2x2"
                tvEsS.text = "Eş Kartlar: 0/4"
            }
            BoardSize.MEDIUM -> {
                tvHamleS.text = "Orta: 4x4"
                tvEsS.text = "Eş Kartlar: 0/16"
            }
            BoardSize.HARD -> {
                tvHamleS.text = "Zor: 6x6"
                tvEsS.text = "Eş Kartlar: 0/36"
            }
        }
        tvEsS.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        memoryGame= MemoryGame(boardSize)
        adapter= MemoryBoardAdapter(this,boardSize, memoryGame.cards, object :MemoryBoardAdapter.CardClickedListener{
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }

        })
        rvBoard.adapter=adapter
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager= GridLayoutManager(this,boardSize.getWidth())
    }

    private fun updateGameWithFlip(position: Int) {
        if(memoryGame.haveWonGame()){
            Snackbar.make(clRoot,"Zaten Kazandınız!",Snackbar.LENGTH_LONG).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            Snackbar.make(clRoot,"Geçersiz Hamle!",Snackbar.LENGTH_SHORT).show()
            return
        }
        if(memoryGame.flipCard(position)){
            Log.i(TAG,"Eşleşme Doğru! Eşleşen Kart Sayısı: ${memoryGame.numPairsFound}")
            val color = ArgbEvaluator().evaluate(
                memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this,R.color.color_progress_none),
                ContextCompat.getColor(this,R.color.color_progress_full),
                ) as Int
            tvEsS.setTextColor(color)
            tvEsS.text= "Eşleşen Kart Sayısı: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if( memoryGame.haveWonGame()){
                Snackbar.make(clRoot,"Tebrikler! Kazandınız..",Snackbar.LENGTH_LONG).show()
            }
        }
        tvHamleS.text = "Hamle Sayısı: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}