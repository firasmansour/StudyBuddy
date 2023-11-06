package com.example.finalproject.groupMainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.databinding.FragmentWeekViewBinding
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.AppUtils.daysInWeekArray
import com.example.finalproject.utils.AppUtils.monthYearFromDate
import com.example.finalproject.utils.DaysRvAdapter
import java.time.LocalDate


class WeekViewFragment : Fragment() ,DaysRvAdapter.onItemsListener{
    private lateinit var binding: FragmentWeekViewBinding
    private lateinit var daysRvAdapter :DaysRvAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWeekViewBinding.inflate(inflater, container, false)
        AppUtils.selectedDate = LocalDate.now()
        setWeekView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.leftBtn.setOnClickListener {
            AppUtils.selectedDate = AppUtils.selectedDate?.minusWeeks(1)
            setWeekView()
        }
        binding.rightBtn.setOnClickListener {
            AppUtils.selectedDate = AppUtils.selectedDate?.plusWeeks(1)
            setWeekView()
        }



    }
    private fun setWeekView() {
        binding.monthYearTV.setText(monthYearFromDate(AppUtils.selectedDate!!))
        val days: ArrayList<LocalDate> = daysInWeekArray(AppUtils.selectedDate!!)!!
        daysRvAdapter = DaysRvAdapter(days)
        binding.calendarRecyclerView.layoutManager = GridLayoutManager(context, 7)
        binding.calendarRecyclerView.adapter = daysRvAdapter
        daysRvAdapter.setOnItemsListener(this)
//        setEventAdpater()
    }

    override fun onItemClick(date: LocalDate) {
        AppUtils.selectedDate = date
        setWeekView()
    }


}