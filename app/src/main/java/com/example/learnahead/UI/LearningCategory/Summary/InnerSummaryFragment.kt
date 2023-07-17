package com.example.learnahead.UI.LearningCategory.Summary

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.learnahead.Data.Model.LearningCategory
import com.example.learnahead.Data.Model.Summary
import com.example.learnahead.Data.Model.User
import com.example.learnahead.R
import com.example.learnahead.UI.Auth.AuthViewModel
import com.example.learnahead.Util.UiState
import com.example.learnahead.Util.hide
import com.example.learnahead.Util.show
import com.example.learnahead.Util.toast
import com.example.learnahead.databinding.FragmentInnerSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher


/**
 * Ein Fragment, das die innere Zusammenfassung darstellt.
 * Es ermöglicht Benutzern das Anzeigen und Bearbeiten von Zusammenfassungen einer Lernkategorie.
 * Benutzer können den Inhalt der Zusammenfassung bearbeiten und Änderungen speichern.
 */
@AndroidEntryPoint
class InnerSummaryFragment : Fragment() {

    private var currentUser: User? = null
    private var currentLearningCategory: LearningCategory? = null
    lateinit var binding: FragmentInnerSummaryBinding
    private val summaryViewModel: SummaryViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var currentSummary: Summary? = null

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
        binding = FragmentInnerSummaryBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Initialisiert die View und setzt die Listener auf die Schaltflächen.
     * Ruft die updateUI()-Methode auf, um die View mit den vorhandenen Daten zu aktualisieren.
     * Ruft die observer()-Methode auf, um die Observer zu initialisieren.
     * Ruft die initEditor()-Methode auf, um den Texteditor zu initialisieren.
     * @param view Die erstellte View.
     * @param savedInstanceState Das Bundle-Objekt, das den zuletzt gespeicherten Zustand enthält.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentSummary = arguments?.getParcelable("summary")
        setLocalCurrentUser()
        setEventListener()
        updateUI()
        observer()
        initEditor()
        if(arguments?.getBoolean("markdown") == true){
            AlertDialog.Builder(requireContext())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Markdown")
                .setMessage("Möchtest du ein Markdown Tutorial?")
                .setPositiveButton("Ja") { _, _ ->
                    if (currentSummary?.content?.isNotEmpty() == true) {
                        binding.markdownEditText.setText(currentSummary?.content)
                    } else {
                        binding.markdownEditText.setText(
                            "# Markdown-Anleitung\n" +
                                    "\n" +
                                    "## Was ist Markdown?\n" +
                                    "Markdown ist eine einfache Auszeichnungssprache, mit der du Text in ein strukturiertes Format umwandeln kannst. Es ist sehr beliebt für das Schreiben von Dokumentationen, README-Dateien und Webseiten. \n" +
                                    "\n" +
                                    "## Textformatierung\n" +
                                    "### Überschriften\n" +
                                    "Du kannst Überschriften mit einem oder mehreren Hash-Zeichen (`#`) erstellen. Je nach Anzahl der Hash-Zeichen wird die Größe der Überschrift festgelegt. Zum Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "# Überschrift der Ebene 1\n" +
                                    "## Überschrift der Ebene 2\n" +
                                    "### Überschrift der Ebene 3\n" +
                                    "```\n" +
                                    "\n" +
                                    "### Absätze\n" +
                                    "Einen Absatz kannst du einfach durch eine Leerzeile erstellen. Markdown fügt automatisch Absätze ein, wenn du eine Leerzeile zwischen zwei Textblöcken lässt.\n" +
                                    "\n" +
                                    "### Fett und kursiv\n" +
                                    "Um Text fett darzustellen, umgibst du ihn mit zwei Sternchen (`**`) oder zwei Unterstrichen (`__`). Um Text kursiv darzustellen, verwendest du ein Sternchen (`*`) oder einen Unterstrich (`_`). Zum Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "**fetter Text**\n" +
                                    "*kursiver Text*\n" +
                                    "```\n" +
                                    "\n" +
                                    "### Aufzählungslisten\n" +
                                    "Erstelle eine Aufzählungsliste, indem du jede Listenelement mit einem Bindestrich (`-`) oder einem Sternchen (`*`) beginnst. Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "- Erstes Element\n" +
                                    "- Zweites Element\n" +
                                    "- Drittes Element\n" +
                                    "```\n" +
                                    "\n" +
                                    "### Nummerierte Listen\n" +
                                    "Erstelle eine nummerierte Liste, indem du jedes Listenelement mit einer Zahl und einem Punkt beginnst. Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "1. Erstes Element\n" +
                                    "2. Zweites Element\n" +
                                    "3. Drittes Element\n" +
                                    "```\n" +
                                    "\n" +
                                    "### Links\n" +
                                    "Du kannst Links erstellen, indem du den Linktext in eckigen Klammern (`[]`) und die URL in runden Klammern (`()`) angibst. Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "[Linktext](https://www.example.com)\n" +
                                    "```\n" +
                                    "\n" +
                                    "### Bilder\n" +
                                    "Füge Bilder hinzu, indem du den Alternativtext in eckigen Klammern (`[]`) und den Pfad oder die URL des Bildes in runden Klammern (`()`) angibst. Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "![Alternativtext](pfad/zum/bild.jpg)\n" +
                                    "```\n" +
                                    "\n" +
                                    "## Erweiterte Formatierung\n" +
                                    "Markdown unterstützt auch erweiterte Formatierungsmöglichkeiten wie Tabellen und Zitate. Hier ist eine kurze Zusammenfassung:\n" +
                                    "\n" +
                                    "### Tabellen\n" +
                                    "Du kannst Tabellen erstellen, indem du die Zellen und deren Inhalt mit senkrechten Strichen (`|`) trennst. Die erste Zeile wird normalerweise für die Tabellenüberschriften verwendet, und die zweite Zeile enthält Trennlinien. Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "| Spalte 1 | Spalte 2 |\n" +
                                    "|----------|----------|\n" +
                                    "| Inhalt 1 | Inhalt 2 |\n" +
                                    "| Inhalt 3 | Inhalt 4 |\n" +
                                    "```\n" +
                                    "\n" +
                                    "### Zitate\n" +
                                    "Um Zitate zu erstellen, füge den zitierten Text mit einem Größer-als-Zeichen (`>`) am Anfang jeder Zeile ein. Beispiel:\n" +
                                    "\n" +
                                    "```\n" +
                                    "> Das ist ein Zitat.\n" +
                                    "> Es kann über mehrere Zeilen gehen.\n" +
                                    "```\n" +
                                    "\n" +
                                    "Das war eine grundlegende Einführung in Markdown. Es gibt noch viele weitere Funktionen und Möglichkeiten, die du erkunden kannst. Viel Spaß beim Schreiben mit Markdown!"
                        )
                    }
                }
                .setNegativeButton("Nein") { _, _ ->
                }
                .show()
        }

    }

    /**
     * Holt den aktuellen Benutzer aus der Datenbank.
     */
    private fun setLocalCurrentUser() {
        authViewModel.getSession()
    }

    /**
     * Aktualisiert die Benutzeroberfläche basierend auf den vorhandenen Daten.
     * Setzt den Text des Labels mit dem Namen der aktuellen Lernkategorie.
     * Setzt den Text des Texteditors mit dem Inhalt der aktuellen Zusammenfassung,
     * oder einen Standardtext, wenn keine Zusammenfassung vorhanden ist.
     */
    private fun updateUI() {
        currentLearningCategory = arguments?.getParcelable("learning_category")
        binding.headerLabel.text = currentLearningCategory?.name
        if (currentSummary?.content?.isNotEmpty() == true) {
            binding.markdownEditText.setText(currentSummary?.content)
        }
    }

    /**
     * Initialisiert den Texteditor für die Bearbeitung des Zusammenfassungsinhalts.
     */
    private fun initEditor() {
        val markwon = context?.let { Markwon.create(it) }
        val editor = markwon?.let { MarkwonEditor.create(it) }
        binding.markdownEditText.addTextChangedListener(
            editor?.let {
                MarkwonEditorTextWatcher.withProcess(
                    editor
                )
            }
        )
    }

    /**
     * Initialisiert die Observer für die Beobachtung des aktuellen Benutzers und der Zusammenfassungsaktualisierung.
     * Der Observer für currentUser überwacht den Zustand des aktuellen Benutzers und führt entsprechende Aktionen aus.
     * Der Observer für updateSummary überwacht den Zustand der Aktualisierung einer Zusammenfassung und führt entsprechende Aktionen aus.
     */
    private fun observer() {
        authViewModel.currentUser.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Fortschrittsanzeige anzeigen
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    // Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    currentUser = state.data
                }
            }
        }

        summaryViewModel.updateSummary.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    // Fortschrittsanzeige anzeigen
                    binding.progressBar.show()
                }
                is UiState.Failure -> {
                    // Fortschrittsanzeige ausblenden und Fehlermeldung anzeigen
                    binding.progressBar.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    if (state.data != null && currentUser != null) {
                        // Die neue Zusammenfassung dem Benutzer hinzufügen
                        var foundSummaryIndex: Int = 0
                            foundSummaryIndex = currentLearningCategory!!.summaries.indexOfFirst { it.id == state.data.id }

                        currentLearningCategory?.summaries?.set(foundSummaryIndex, state.data)
                        val foundIndex =
                            currentUser!!.learningCategories.indexOfFirst { it.id == currentLearningCategory?.id }
                        if (foundIndex != -1) {
                            currentUser!!.learningCategories[foundIndex] = currentLearningCategory!!
                        }
                        // Den Benutzer in der Datenbank aktualisieren
                        authViewModel.updateUserInfo(currentUser!!)
                        findNavController().navigate(
                            R.id.action_innerSummaryFragment_to_summaryPreviewFragment,
                            Bundle().apply {
                                putParcelable("summary", currentSummary)
                                putParcelable("learning_category", currentLearningCategory)
                            }
                        )
                        toast("Die Zusammenfassung konnte erfolgreich aktualisiert werden")
                    } else {
                        toast("Die Zusammenfassung konnte nicht aktualisiert werden")
                    }
                }
            }
        }
    }

    /**
     * Erstellt alle notwendigen EventListener für das Fragment.
     * Setzt die Listener für die Schaltflächen und führt die entsprechenden Aktionen aus.
     */
    private fun setEventListener() {
        // Klick Listener zum Weiterleiten auf den Home Screen
        binding.homeButton.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_homeFragment) }

        // Klick Listener zum Weiterleiten auf den Lernkategorien Screen
        binding.learningCategoriesButton.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_learningCategoryListFragment) }

        // Klick Listener zum Weiterleiten auf den Lernzielen Screen
        binding.learningGoalsButton.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_goalListingFragment) }

        // Klick Listener zum Ausloggen des Benutzers
        binding.logoutIcon.setOnClickListener {
            authViewModel.logout {
                AlertDialog.Builder(requireContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Ausloggen")
                    .setMessage("Möchtest du dich wirklich ausloggen?")
                    .setPositiveButton("Ja") { _, _ ->
                        findNavController().navigate(R.id.action_innerSummaryFragment_to_loginFragment)
                    }
                    .setNegativeButton("Nein", null)
                    .show()
            }
        }

        // Klick Listener für den Info Knopf
        binding.infoIcon.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Information")
                .setMessage("Dies hier ist ein Markdown Editor. Falls du Markdown noch nicht kennst, kannst du ja mal das Tutorial ausprobieren.\nMit drücken auf \"Vorschau\" speicherst du deinen Input.")
                .setPositiveButton("Danke!") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

        }

        // Klick Listener zum Speichern der Zusammenfassung
        binding.previewButton.setOnClickListener {
            currentSummary?.content = binding.markdownEditText.text.toString()
            currentSummary?.let { summary -> summaryViewModel.updateSummary(summary) }
        }

        // Klick Listener zum Zurücknavigieren zur Zusammenfassungsliste
        binding.backIcon.setOnClickListener { findNavController().navigate(R.id.action_innerSummaryFragment_to_summaryFragment,
            Bundle().apply {
                putParcelable("summary", currentSummary)
                putParcelable("learning_category", currentLearningCategory)
            })
        }
    }
}
