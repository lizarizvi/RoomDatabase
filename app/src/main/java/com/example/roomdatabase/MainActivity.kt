package com.example.roomdatabase

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.R
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabase.databinding.ActivityMainBinding
import com.example.roomdatabase.databinding.DialogUpdateBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val employeeDao = (application as EmployeeApp).db.employeeDao()
        binding?.btnAddRecord?.setOnClickListener {
            addRecord(employeeDao)
        }

        lifecycleScope.launch{
            employeeDao.fetchAllEmployees().collect{
                val list = ArrayList(it)
                setUpList(list, employeeDao)
            }
        }
    }

    private fun addRecord(employeeDao: EmployeeDao){
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmail?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()){
            lifecycleScope.launch{
                employeeDao.insert(EmployeeEntity(name=name, email=email))

                Toast.makeText(applicationContext, "Record saved.", Toast.LENGTH_LONG).show()

                binding?.etName?.text?.clear()
                binding?.etEmail?.text?.clear()
            }
        }else{
            Toast.makeText(applicationContext, "Enter name and email", Toast.LENGTH_LONG).show()
        }
    }

    private fun setUpList(employeesList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao){
        if (employeesList.isNotEmpty()){
            val itemAdapter = ItemAdapter(employeesList, {
                updateId->
                updateRecord(updateId, employeeDao)
            },
                {
                    deleteId->
                    deleteRecord(deleteId, employeeDao)
                })
            binding?.rvItems?.layoutManager = LinearLayoutManager(this)
            binding?.rvItems?.adapter = itemAdapter
            binding?.rvItems?.visibility = View.VISIBLE
            binding?.tvNoRecords?.visibility = View.GONE
        }else{
            binding?.rvItems?.visibility = View.GONE
            binding?.tvNoRecords?.visibility = View.VISIBLE
        }
    }

    private fun updateRecord(id: Int, employeeDao: EmployeeDao){
        val updateDialog = Dialog(this, R.style.Theme_AppCompat_Dialog)
        updateDialog.setCancelable(false)
        val binding = DialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)
        lifecycleScope.launch{
            employeeDao.fetchAllEmployeeById(id).collect{
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
            }
        }
        binding.btnUpdate.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty()){
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name,email))
                    Toast.makeText(applicationContext, "Record updated.", Toast.LENGTH_LONG).show()
                    updateDialog.dismiss()
                }
            }else
            {
                Toast.makeText(applicationContext, "Enter name and email", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnCancel.setOnClickListener {
            updateDialog.dismiss()
        }
        updateDialog.show()
    }

    private fun deleteRecord(id: Int, employeeDao: EmployeeDao){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setPositiveButton("YES"){
            dialogInterface,_->
            lifecycleScope.launch{
                employeeDao.delete(EmployeeEntity(id))
                Toast.makeText(applicationContext, "Record deleted.", Toast.LENGTH_LONG).show()
            }
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("NO"){
                dialogInterface,_->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}