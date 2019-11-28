package com.fapce.nicholas.jogodavelha

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_payment.*
import org.json.JSONObject

class PaymentActivity : AppCompatActivity() {

    private var asyncTask: PaymentTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        btnCancel.setOnClickListener {
            finish()
        }
    }





    fun pay(view: View) {

        val pagar = JSONObject().apply {
            put("numero_cartao", edtCardNum.text.toString())
            put("nome_cliente", edtName.text.toString())
            put("bandeira", edtBrand.text.toString())
            put("cod_seguranca", edtSecurityCode.text.toString())
            put("valor_em_centavos", 500)
//            put("parcelas", 1)
//            put("cod_loja", "loja-01")
        }

        var respostaWS = 0 // ???

        if (CepHttp.hasConnection(this)) {
            if (asyncTask?.status != AsyncTask.Status.RUNNING) {
                if(edtCardNum.text.toString() == "0000.1111.1111.1111" &&
                    edtName.text.toString() == "nicholas" &&
                    edtSecurityCode.text.toString() == "4321" &&
                    edtBrand.text.toString() == "master") {
                    asyncTask = PaymentTask()
                    asyncTask?.execute("pay")
                    //asyncTask?.execute(JSON pagar)//////// precisa colocar o json aqui para ser enviado
                    respostaWS = 200
                }else{
                    asyncTask?.execute("error")
                    respostaWS = 401

                }
            } else {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this, "Erro na conexão \n", Toast.LENGTH_LONG
                ).show()
            }
        }

        // mandar para a tela de pagamento com o web service
        //            WEB SERVICE...........................................
        // se respostaWS == 200, enviar para a tela de jogo
        // se respostaWS == 401, enviar para tela de login

        val arquivo = getSharedPreferences("control_login", Context.MODE_PRIVATE)
        val editor = arquivo.edit()

        if (respostaWS == 200) {
            editor.putInt("qt_login", 1)
            editor.commit()

            Toast.makeText(
                this, "Obrigado por continuar jogando conosco! \n", Toast.LENGTH_LONG
            ).show()

            val intent = Intent(this, PlayersActivity::class.java)
            startActivity(intent)
        } else if (respostaWS == 401) {
            Toast.makeText(
                this, "Não pagou, não joga! \n", Toast.LENGTH_LONG
            ).show()

            // Redirecionando para a tela de login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    // ---------------------------------------------------------------------------------------------

    fun btnCEP(view: View) {
        if (CepHttp.hasConnection(this)) {
            if (asyncTask?.status != AsyncTask.Status.RUNNING) {
                asyncTask = PaymentTask()
                asyncTask?.execute("ola")
                //asyncTask?.execute(JSON pagar)//////// precisa colocar o json aqui para ser enviado
            }else{
                asyncTask?.execute("erro")
            }
        } else {
         progressBar.visibility = View.GONE
            Toast.makeText(
                this, "Erro na conexão \n", Toast.LENGTH_LONG
            ).show()
        }

    }

    // 1o. Parâmetro: Entrada do método doInBackground()
    // 2o. Parâmetro: Indicativo de progresso da tarefa - chamar o método publishProgress() dentro do método doInBackground()
    // 3o. Parâmetro: Saída do método doInBackground()
    inner class PaymentTask : AsyncTask<String, Void, Pay>() {

        ////Os progressBar é para fazer a bolinha girando enquanto espera a requisição

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg strings: String?): Pay? {
            return CepHttp.loadCep(strings[0])
        }

    }


}
