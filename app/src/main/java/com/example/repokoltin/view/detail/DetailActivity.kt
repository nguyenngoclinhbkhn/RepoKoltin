package com.example.repokoltin.view.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.repokoltin.R
import com.example.repokoltin.Utils
import com.example.repokoltin.view.main.MainActivity
import com.example.repokoltin.model.RepoRealm
import com.example.repokoltin.presenter.PresenterDetail
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(), DetailView {
    private lateinit var presenterDetail: PresenterDetail
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        Realm.init(this)
        val id = intent.getStringExtra(Utils.KEY).toInt()
        presenterDetail = PresenterDetail(this)
        presenterDetail.showData(id)
        txtDelete.setOnClickListener {
            presenterDetail.deleteRepo(id)
        }
    }

    override fun onStop() {
        super.onStop()
        presenterDetail.stop()
    }

    override fun onBackPressed() {
        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
        finish()
    }

    override fun onShowDetail(t: RepoRealm) {
        txtFullName.text = (t.fullname)
        txtDes.text = t.des
        txtStar.text = t.star
        txtForks.text = t.fork
        txtLanguage.text = t.lang
        txtTitleToolbar.text = t.fullname
    }

    override fun onDeleteRepoSuccess() {
        startActivity(Intent(this@DetailActivity, MainActivity::class.java))
        finish()
    }

    override fun showDetailFaild() {
        Toast.makeText(this, "Show detail error", Toast.LENGTH_SHORT).show()
    }
}
