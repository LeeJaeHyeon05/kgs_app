package com.example.kgs_app.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.example.kgs_app.R
import com.example.kgs_app.adapter.ViewPagerAdapter
import com.example.kgs_app.databinding.ActivityMainBinding
import com.example.kgs_app.dialog.DialogActivity
import com.example.kgs_app.dialog.MenuDialogActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.ncorti.slidetoact.SlideToActView

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // loginactivity에서 가지고 오는 데이터
        val receivedName = intent.getStringExtra("student-name")


        // 메인 환영합니다 텍스트 채우기
        setWelcomeText(receivedName)
        //그 유저 데이터 가지고 오기
        getData(receivedName)
        //뷰페이저 함수
        mainViewPager()
        // 커스텀 다이얼로그 함수
        popDialog(receivedName)
        // 메뉴 다이얼로그 함수
        menuDialog(receivedName)
        //오더 현황 보기
        orderControl()
        // 선생님 연락
        binding.emergencyCard.setOnClickListener {
            startActivity(Intent(this, TeachersActivity::class.java))
        }
        // 메인 assets 값 가지고 오기
        val docRef = receivedName?.let { db.collection("student").document(it) }
        docRef?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val asset = snapshot.data!!["assets"].toString()
                binding.BalanceNumberTextView.text = addCommasToNumber(asset)
            }
        }

    }
    private fun addCommasToNumber(number: Any?): String {
        // 숫자를 문자열로 변환
        var numberString = number.toString()

        // 천 단위마다 쉼표 추가
        val stringBuilder = StringBuilder()
        var count = 0
        for (i in numberString.length - 1 downTo 0) {
            stringBuilder.append(numberString[i])
            count++
            if (count == 3 && i != 0) {
                stringBuilder.append(",")
                count = 0
            }
        }

        // 거꾸로 된 문자열을 다시 뒤집어서 원래 순서로 되돌리기
        return stringBuilder.reverse().toString()
    }
    private fun orderControl() {
        binding.orderControlCard.setOnClickListener {

            startActivity(Intent(this, ControlActivity::class.java))
        }
    }
    private fun setWelcomeText(receivedName: String?) {
        receivedName?.let {
            db.collection("student")
                .document(it)
                .get()
                .addOnSuccessListener {document ->
                    if (document.exists()) {
                        val getName = document.getString("name")
                        // 텍스트 채우기
                        binding.welcomeTextView.text = "안녕하세요! ${getName}님"
                    }
                }
                //데이터베이스 조회 실패 했을때
                .addOnFailureListener {
                    Toast.makeText(this, getString(R.string.failCheckDatabase), Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun popDialog(receivedName: String?) {
        val slideCard = binding.slideToCard
        slideCard.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                val dialog = DialogActivity(this@MainActivity)
                dialog.myDia(receivedName)
                slideCard.setCompleted(completed = false, withAnimation = true)
            }
        }
    }
    private fun menuDialog(receivedName: String?) {
        //메뉴
        val slideMenu = binding.slideToMenu
        slideMenu.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                val dialog = MenuDialogActivity(this@MainActivity)
                dialog.menuDia(receivedName)
                slideMenu.setCompleted(completed = false, withAnimation = true)
            }
        }
    }
    private fun mainViewPager() {
        val picArr = arrayListOf<Int>(
            R.drawable.ptwo,
            R.drawable.pthree,
            R.drawable.pfour,
            R.drawable.pfive
        )
        // 메인 사진 뷰페이저
        val viewpager = binding.viewpager
        viewpager.adapter = ViewPagerAdapter(picArr) // 어댑터 생성
        viewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로

    }

    //데이터 가지고오기
    private fun getData(receivedName: String?) {
        val nameFrom = receivedName.toString()
        db.collection("student")
            .document(nameFrom)
            .get()
            .addOnSuccessListener {document ->
                if (document.exists()) {
                    val studentName = document.getString("name")
                    val studentClass= document.getString("class")
                    val studentGrade = document.getString("grade")
                    val studentBirth = document.getString("birth")

                    if (studentName != null) {
                        Log.d("dfd", studentName)
                    }
                    if (studentClass != null) {
                        Log.d("dfd", studentClass)
                    }
                    if (studentGrade != null) {
                        Log.d("dfd", studentGrade)
                    }
                    if (studentBirth != null) {
                        Log.d("dfd", studentBirth)
                    }

                }

            }
    }
}