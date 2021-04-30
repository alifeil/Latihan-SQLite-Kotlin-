package com.example.latihansqlite

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_update.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupListOfDataIntoRecyclerView()
        btnAdd.setOnClickListener {
            addRecord()
            closekeyboard()
        }
        setupListOfDataIntoRecyclerView()
    }

    private fun addRecord() {
        val name = namaku.text.toString()
        val email = emailku.text.toString()
        val phone = phoneku.text.toString()
        val address = addressku.text.toString()

        val databaseHandler: DatabaseHandler =
            DatabaseHandler(this)
        if (!name.isEmpty() && !email.isEmpty()) {
            val status = databaseHandler.addEmployee(empmodelclass(0, name, email,phone,address))
            if (status > -1) {
                Toast.makeText(this, "Record Saved", Toast.LENGTH_SHORT).show()
                namaku.text.clear()
                emailku.text.clear()
                phoneku.text.clear()
                addressku.text.clear()
            }
        } else {
            Toast.makeText(this, "Name or Email cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method untuk mendapatkan jumlah record
     */
    private fun getItemsList(): ArrayList<empmodelclass> {
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val empList: ArrayList<empmodelclass> = databaseHandler.viewEmployee()
        return empList
    }

    /**
     * method untuk menampilkan emlist ke recycler view
     */
    private fun setupListOfDataIntoRecyclerView() {
        if (getItemsList().size > 0) {
            recyclerviewku.visibility = View.VISIBLE
            norecordku.visibility = View.GONE

            recyclerviewku.layoutManager = LinearLayoutManager(this)
            recyclerviewku.adapter = ItemAdapter(this, getItemsList())
        } else {
            recyclerviewku.visibility = View.GONE
            norecordku.visibility = View.VISIBLE
        }
    }

    /**
     * Method untuk menampilkan dialog konfirmasi delete
     */
    fun deleteRecordAlertDialog(empmodelclass: empmodelclass) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Delete Record")
        builder.setMessage("Are you sure?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //menampilkan tombol yes
        builder.setPositiveButton("Yes") { dialog: DialogInterface, which ->
            val databaseHandler :DatabaseHandler = DatabaseHandler(this)
            val status = databaseHandler.deleteEmployee(empmodelclass(empmodelclass.id,"","","",""))

            if (status > -1){
                Toast.makeText(this, "Record Deleted Successfully", Toast.LENGTH_LONG).show()
                setupListOfDataIntoRecyclerView()
            }

            dialog.dismiss()
        }
        //menampilkan tombol no
        builder.setNegativeButton("No") { dialog: DialogInterface, which ->

            dialog.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        //memastikan user menekan tombol yes or no
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    /**
     * method to show custom update dialog
     */
    fun updateRecordDialog(empmodelclass: empmodelclass){
        val updatedialog = Dialog(this, R.style.Theme_Dialog)

        updatedialog.setCancelable(false)
        updatedialog.setContentView(R.layout.dialog_update)

        updatedialog.etUpdateName.setText(empmodelclass.name)
        updatedialog.etUpdateEmail.setText(empmodelclass.email)
        updatedialog.etUpdatePhone.setText(empmodelclass.phone)
        updatedialog.etUpdateAddress.setText(empmodelclass.address)

        updatedialog.tvUpdated.setOnClickListener {
            val name = updatedialog.etUpdateName.text.toString()
            val email = updatedialog.etUpdateEmail.text.toString()
            val phone = updatedialog.etUpdatePhone.text.toString()
            val address = updatedialog.etUpdateAddress.text.toString()

            val databaseHandler : DatabaseHandler = DatabaseHandler(this)

            if(!name.isEmpty() && !email.isEmpty()){
                val status = databaseHandler.updateEmployee(empmodelclass(empmodelclass.id,name,email,phone,address))
                if(status > -1){
                    Toast.makeText(this,"Record updated", Toast.LENGTH_LONG).show()
                    setupListOfDataIntoRecyclerView()
                    updatedialog.dismiss()
                    closekeyboard()
                }
            }else{
                Toast.makeText(this,"Name or Email cannot be blank", Toast.LENGTH_LONG).show()
            }
        }
        updatedialog.tvCancel.setOnClickListener {
            updatedialog.dismiss()
        }
        updatedialog.show()
    }
    /**
     * method class keyboard
     */
    private fun closekeyboard(){
        val view = this.currentFocus
        if (view != null){
            val imn = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imn.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
}
