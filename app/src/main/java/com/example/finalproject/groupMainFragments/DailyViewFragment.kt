package com.example.finalproject.groupMainFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalproject.GroupRoomActivity
import com.example.finalproject.databinding.FragmentDailyViewBinding
import com.example.finalproject.utils.AppUtils
import com.example.finalproject.utils.AppUtils.selectedDate
import com.example.finalproject.utils.DayTasksRvAdapter
import com.example.finalproject.utils.DaysRvAdapter
import com.example.finalproject.utils.Group
import com.example.finalproject.utils.Task
import com.example.finalproject.utils.WeeklyTasksRvAdapter
import java.time.format.TextStyle
import java.util.Locale


class DailyViewFragment : Fragment() ,DayTasksRvAdapter.OnTaskClickListener,ShowTaskPopUpFragment.ShowPdfDialogListener{
    private lateinit var binding :FragmentDailyViewBinding
    private lateinit var dayTasksRvAdapter: DayTasksRvAdapter
    private lateinit var group: Group


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyViewBinding.inflate(inflater, container, false)
        group = arguments?.getParcelable<Group>("group")!!



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDayView()
        binding.leftBtn.setOnClickListener {
            selectedDate = selectedDate?.minusDays(1)
            setDayView()
        }
        binding.rightBtn.setOnClickListener {
            selectedDate = selectedDate?.plusDays(1)
            setDayView()
        }
    }

    private fun setDayView() {
        binding.monthDayTV.text = AppUtils.monthDayFromDate(selectedDate!!)
        binding.dayOfWeekTV.text = selectedDate!!.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())
        setHourAdapter();


    }

    private fun setHourAdapter() {
        val tasksForTheDay = AppUtils.dailyViewTasks(group.tasksMap,selectedDate)
        dayTasksRvAdapter = DayTasksRvAdapter(tasksForTheDay)
        binding.hourListTasksRv.layoutManager =  LinearLayoutManager(context)
        binding.hourListTasksRv.adapter = dayTasksRvAdapter
        dayTasksRvAdapter.setOnTaskClickListener(this)
    }

    override fun onTaskClick(task: Task) {
        val popupDialog = ShowTaskPopUpFragment(task.title,task.description,task.date,task.pdfName,task.pdfLink)
        popupDialog.setShowPdfDialogListener(this)
        popupDialog.show(childFragmentManager, "ShowTaskPopUp")
    }

    override fun onShowPdf(pdfName: String, pdfLink: String) {
        val pdfViewerFragment = PdfViewerFragment(pdfName,pdfLink)
        (activity as? GroupRoomActivity)?.setCurrFragment(pdfViewerFragment)
    }


}