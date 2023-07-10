package com.example.learnahead_prototyp.UI.Summary

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import com.example.learnahead_prototyp.Data.Model.User
import com.example.learnahead_prototyp.UI.Auth.AuthViewModel
import com.example.learnahead_prototyp.UI.Goal.SummaryViewModel
import com.example.learnahead_prototyp.databinding.FragmentSummaryPreviewBinding
import androidx.fragment.app.viewModels
import com.example.learnahead_prototyp.Data.Model.Summary
import androidx.navigation.fragment.findNavController
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.TextView
import com.example.learnahead_prototyp.Util.UiState
import com.example.learnahead_prototyp.Util.hide
import com.example.learnahead_prototyp.Util.show
import com.example.learnahead_prototyp.Util.toast
import com.example.learnahead_prototyp.R
import androidx.core.widget.doAfterTextChanged
import com.example.learnahead_prototyp.Data.Model.LearningCategory
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;


@AndroidEntryPoint
class summaryPreviewFragment : Fragment() {

    private var currentUser: User? = null
    private var currentLearningCategory: LearningCategory?= null
    lateinit var binding: FragmentSummaryPreviewBinding
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var isEdit = false
    private var objSummary: Summary? = null
    private var markdownOutput: String = ""

    /**
     * Erstellt die View und gibt sie zurück.
     * @param inflater Der LayoutInflater, der verwendet wird, um die View zu erstellen.
     * @param container Der ViewGroup-Container, der die View enthält.
     * @param savedInstanceState Das Bundle-Objekt, das den zuletzt gespeicherten Zustand enthält.
     * @return Die erstellte View.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryPreviewBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Initialisiert die View und setzt die Listener auf die Schaltflächen. Ruft die UpdateUI()-Methode auf,
     * um die View mit den vorhandenen Daten zu aktualisieren.
     * @param view Die erstellte View.
     * @param savedInstanceState Das Bundle-Objekt, das den zuletzt gespeicherten Zustand enthält.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLocalCurrentUser()
        setEventListener()
        parseMarkdown()
    }

    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    fun convertMarkdownToHtml(markdown: String): String {
        val parser = Parser.builder().build()
        val document = parser.parse(markdown)

        val renderer = HtmlRenderer.builder().build()
        val html = renderer.render(document)

        return html
    }
    fun displayMarkdownInTextView(markdownText: String, textView: TextView) {
        val htmlText = convertMarkdownToHtml(markdownText)
        val spannedText = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)

        textView.text = spannedText
    }
    private fun parseMarkdown() {
        markdownOutput = arguments?.getString("markdownInputText").toString()
        displayMarkdownInTextView(markdownOutput, binding.markdownViewText)
    }



    /**
     * Erstellt alle notwendigen EventListener für das Fragment
     */
    private fun setEventListener() {
        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.buttonHome.setOnClickListener { findNavController().navigate(R.id.action_summaryPreviewFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.buttonLearningCategories.setOnClickListener { findNavController().navigate(R.id.action_summaryPreviewFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.buttonLearningGoals.setOnClickListener { findNavController().navigate(R.id.action_summaryPreviewFragment_to_goalListingFragment) }

        // Setzt den Event-Listener für den Logout-Button
        binding.logout.setOnClickListener {
            authViewModel.logout {
                findNavController().navigate(R.id.action_summaryPreviewFragment_to_loginFragment)
            }
        }

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.action_innerSummaryFragment_to_summaryPreviewFragment,
                Bundle().apply {
                    putString("markdownOutputText", binding.markdownViewText.text.toString())
                })
        }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_summaryFragment,
            Bundle().apply {
                putParcelable("learning_category", currentLearningCategory)
            })
        }

    }


}