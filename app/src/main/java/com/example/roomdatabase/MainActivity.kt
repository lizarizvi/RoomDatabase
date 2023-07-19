package com.example.roomdatabase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabase.databinding.ActivityMainBinding
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
            val itemAdapter = ItemAdapter(employeesList)
            binding?.rvItems?.layoutManager = LinearLayoutManager(this)
            binding?.rvItems?.adapter = itemAdapter
            binding?.rvItems?.visibility = View.VISIBLE
            binding?.tvNoRecords?.visibility = View.GONE
        }else{
            binding?.rvItems?.visibility = View.GONE
            binding?.tvNoRecords?.visibility = View.VISIBLE
        }
    }
}