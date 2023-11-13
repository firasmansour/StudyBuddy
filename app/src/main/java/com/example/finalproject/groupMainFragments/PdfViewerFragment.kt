package com.example.finalproject.groupMainFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.finalproject.R
import com.example.finalproject.databinding.FragmentPdfViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


class PdfViewerFragment(private val pdfName:String,private val pdfLink:String) : Fragment() {

    private lateinit var binding:FragmentPdfViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPdfViewerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {

            val inputStream = URL(pdfLink).openStream()
            withContext(Dispatchers.Main) {
                binding.pdfView.fromStream(inputStream).onRender { pages, pageWidth, pageHeight ->
                    if (pages >= 1) {
                        binding.progressBar.visibility = View.GONE
                    }
                }.load()
            }
        }
    }


}