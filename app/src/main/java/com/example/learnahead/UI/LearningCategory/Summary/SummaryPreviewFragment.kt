package com.example.learnahead.UI.LearningCategory.Summary

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.Summary
import com.example.learnahead.Data.Model.User
import com.example.learnahead.R
import com.example.learnahead.UI.Auth.AuthViewModel
import com.example.learnahead.databinding.FragmentSummaryPreviewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.network.NetworkSchemeHandler
import io.noties.markwon.syntax.Prism4jThemeDefault
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Ein [Fragment], das eine Vorschau des Summary anzeigt.
 */
@AndroidEntryPoint
class SummaryPreviewFragment : Fragment() {

    private var currentUser: User? = null
    private var currentLearningCategory: LearningCategory? = null
    private lateinit var binding: FragmentSummaryPreviewBinding
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var isEdit = false
    private var currentSummary: Summary? = null
    private var markdownOutput: String = ""

    /**
     * Erstellt die View-Hierarchie des Fragments.
     * @param inflater Der LayoutInflater, der verwendet wird, um die View-Hierarchie aufzubauen.
     * @param container Der ViewGroup, in die die View-Hierarchie eingefügt wird.
     * @param savedInstanceState Das Bundle, das den Zustand des Fragments enthält.
     * @return Die View-Hierarchie des Fragments.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSummaryPreviewBinding.inflate(inflater)
        return binding.root
    }

    /**
     * Initialisiert die View und setzt die Listener auf die Schaltflächen. Ruft die [updateUI]()-Methode auf,
     * um die View mit den vorhandenen Daten zu aktualisieren.
     * @param view Die erstellte View.
     * @param savedInstanceState Das Bundle-Objekt, das den zuletzt gespeicherten Zustand enthält.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentSummary = arguments?.getParcelable("summary")
        setLocalCurrentUser()
        setEventListener()
        parseMarkdown()
        updateUI()
    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Aktualisiert die Benutzeroberfläche des Fragments.
     */
    private fun updateUI() {
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.headerLabel.text = currentLearningCategory?.name
    }

    /**
     * Konvertiert Markdown-Text in HTML-Text.
     * @param markdown Der Markdown-Text.
     * @return Der HTML-Text.
     */
    private fun convertMarkdownToHtml(markdown: String): String {
        val parser = Parser.builder().build()
        val document = parser.parse(markdown)

        val renderer = HtmlRenderer.builder().build()
        val html = renderer.render(document)

        return html
    }

    /**
     * Zeigt den Markdown-Text in einem TextView an.
     * @param markdownText Der Markdown-Text.
     * @param textView Der TextView, in dem der Markdown-Text angezeigt werden soll.
     */
    private fun displayMarkdownInTextView(markdownText: String, textView: TextView) {
        val htmlText = convertMarkdownToHtml(markdownText)
        val spannedText = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)

        textView.text = spannedText
    }

    /**
     * Parst den Markdown-Text und zeigt ihn an.
     */
    private fun parseMarkdown() {
        val imagesPlugin = ImagesPlugin.create { plugin ->
            plugin.addSchemeHandler(
                NetworkSchemeHandler.create()
            )
        }
        val theme = Prism4jThemeDefault.create()

        val markwon = Markwon.builder(requireContext())
            .usePlugin(imagesPlugin)
            .build()
        currentSummary?.let { markwon.setMarkdown(binding.textMarkdown, it.content) }
    }

    /**
     * Setzt die Event-Listener für die Buttons und Views des Fragments.
     */
    private fun setEventListener() {
        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_summaryPreviewFragment_to_homeFragment) }
        binding.learningCategoriesButton.setOnClickListener { findNavController().navigate(R.id.action_summaryPreviewFragment_to_learningCategoryListFragment) }
        binding.learningGoalsButton.setOnClickListener { findNavController().navigate(R.id.action_summaryPreviewFragment_to_goalListingFragment) }

        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Ausloggen")
                    .setMessage("Möchtest du dich wirklich ausloggen?")
                    .setPositiveButton("Ja") { _, _ ->
                        findNavController().navigate(R.id.action_summaryPreviewFragment_to_loginFragment)
                    }
                    .setNegativeButton("Nein", null)
                    .show()
            }
        }

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.action_summaryPreviewFragment_to_innerSummaryFragment,
                Bundle().apply {
                    putParcelable("learning_category", currentLearningCategory)
                    putParcelable("summary", currentSummary)
                })
        }

        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_summaryPreviewFragment_to_summaryFragment,
            Bundle().apply {
                putParcelable("learning_category", currentLearningCategory)
            })
        }
    }
}