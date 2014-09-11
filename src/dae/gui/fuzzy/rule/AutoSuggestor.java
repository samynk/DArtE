package dae.gui.fuzzy.rule;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.FuzzyVariable;
import mlproject.fuzzy.MemberShip;

public class AutoSuggestor {

    private final JTextComponent textComp;
    private final JDialog container;
    private JList autoSuggestionPopUpWindow;
    private String typedWord;
    private FuzzySystem system;
    private int tW, tH;
    private DocumentListener documentListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            checkForAndShowSuggestions();
        }
    };
    private final Color suggestionsTextColor;
    private final Color suggestionFocusedColor;
    private DefaultListModel<String> suggestionListModel;

    public AutoSuggestor(JTextComponent textComp, JDialog mainWindow, FuzzySystem system, Color popUpBackground, Color textColor, Color suggestionFocusedColor, float opacity) {
        this.textComp = textComp;
        this.suggestionsTextColor = textColor;
        this.container = mainWindow;
        this.suggestionFocusedColor = suggestionFocusedColor;

        this.system = system;

        typedWord = "";
        tW = 0;
        tH = 0;

        autoSuggestionPopUpWindow = new JList();
        autoSuggestionPopUpWindow.setFont(textComp.getFont());
        autoSuggestionPopUpWindow.setVisibleRowCount(10);
        autoSuggestionPopUpWindow.setVisible(false);
        autoSuggestionPopUpWindow.setBorder(new LineBorder(Color.BLACK, 1));
        autoSuggestionPopUpWindow.setFocusable(false);
        suggestionListModel = new DefaultListModel<String>();
        autoSuggestionPopUpWindow.setModel(suggestionListModel);
    }

    public void setFuzzySystem(FuzzySystem system) {
        this.system = system;
    }

    public void registerDocumentListener() {
        this.textComp.getDocument().addDocumentListener(documentListener);
    }

    private void checkForAndShowSuggestions() {
        typedWord = getCurrentlyTypedWord();
        String variable = getCurrentVariable();
        suggestionListModel.clear();//remove previos words/jlabels that were added

        //used to calcualte size of JWindow as new Jlabels are added
        tW = 0;
        tH = 0;

        if (variable != null && !variable.isEmpty()) {
            boolean added = doMemberShipSuggestion(variable, typedWord);

            if (!added) {
                if (autoSuggestionPopUpWindow.isVisible()) {
                    autoSuggestionPopUpWindow.setVisible(false);
                }
            } else {
                showPopUpWindow();
            }
        }else{
            autoSuggestionPopUpWindow.setVisible(false);
        }
    }

    protected void addWordToSuggestions(String word) {
        suggestionListModel.addElement(word);
    }

    public String getCurrentlyTypedWord() {//get newest word after last white spaceif any or the first word if no white spaces
        String text = textComp.getText();
        int currentCaretLocation = textComp.getCaretPosition();
        if (currentCaretLocation == 0 || currentCaretLocation > (text.length())) {
            return "";
        }
        int index;
        Document d = textComp.getDocument();
        int count = 0;
        for (index = currentCaretLocation; index >= 0; --index) {
            try {
                char c = d.getText(index, 1).charAt(0);
                if (Character.isWhitespace(c)) {
                    break;
                }
                ++count;
            } catch (BadLocationException ex) {
                break;
            }
        }
        if (index > 0) {
            String currentWord;
            try {
                currentWord = d.getText(index + 1, count);
                return currentWord;
            } catch (BadLocationException ex) {
                return "";
            }
        } else {
            return "";
        }
    }

    private int matchWordReverse( int startLocation) {
        Document d = textComp.getDocument();
        int index;
        for (index = startLocation; index >= 0; --index) {
            try {
                char c = d.getText(index, 1).charAt(0);
                if (Character.isWhitespace(c)) {
                    break;
                }
            } catch (BadLocationException ex) {
                break;
            }
        }
        return index;
    }

    private int matchWhiteSpaceReverse(int startLocation) {
        Document d = textComp.getDocument();
        int index;
        for (index = startLocation; index >= 0; --index) {
            try {
                char c = d.getText(index, 1).charAt(0);
                if (!Character.isWhitespace(c)) {
                    break;
                }
            } catch (BadLocationException ex) {
                break;
            }
        }
        return index;
    }

    public String getCurrentVariable() {
        Document d = textComp.getDocument();
        int currentCaretLocation = textComp.getCaretPosition();
        if (currentCaretLocation == 0 || currentCaretLocation >= d.getLength()) {
            return "";
        }
        // search for "is" or "isnot" in the text before the caretlocation.
        // index of where the current membership starts.
        int index1 = matchWordReverse( currentCaretLocation);
        if (index1 == 0) {
            return "";
        }
        // whitespace between membership and "is" or "isnot"
        int index2 = matchWhiteSpaceReverse(index1);
        if (index2 == 0) {
            return "";
        }
        int index3 = matchWordReverse( index2);
        if (index3 == 0) {
            return "";
        }

        String operator;
        try {
            operator = d.getText(index3+1, index2-index3);
        } catch (BadLocationException ex) {
            return "";
        }
        int index4 = matchWhiteSpaceReverse( index3);
        if (index4 == 0) {
            return "";
        }
        int index5 = matchWordReverse( index4);
        if (index5 == 0) {
            return "";
        }
        String var;
        try {
            var = d.getText(index5+1,index4-index5);
        } catch (BadLocationException ex) {
            return "";
        }
        if (operator.equals("is") || operator.equals("isnot")) {
            return var;
        } else {
            return "";
        }
    }

    private void showPopUpWindow() {
        autoSuggestionPopUpWindow.setSize(autoSuggestionPopUpWindow.getPreferredSize());
        autoSuggestionPopUpWindow.validate();


        int windowX = 0;
        int windowY = 0;

        if (textComp instanceof JTextField) {//calculate x and y for JWindow at bottom of JTextField
            windowX = container.getX() + textComp.getX() + 5;
            if (autoSuggestionPopUpWindow.getHeight() > autoSuggestionPopUpWindow.getMinimumSize().height) {
                windowY = container.getY() + textComp.getY() + textComp.getHeight() + autoSuggestionPopUpWindow.getMinimumSize().height;
            } else {
                windowY = container.getY() + textComp.getY() + textComp.getHeight() + autoSuggestionPopUpWindow.getHeight();
            }
        } else {//calculate x and y for JWindow on any JTextComponent using the carets position
            JTextPane textPane = (JTextPane) textComp;
            Point caretpos = textComp.getCaret().getMagicCaretPosition();
            Point popupLoc = SwingUtilities.convertPoint(textPane, caretpos, container.getLayeredPane());

            FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
            char currentChar = textComp.getText().charAt(textComp.getCaretPosition());

            windowX = popupLoc.x + fm.charWidth(currentChar);
            windowY = popupLoc.y;
        }

        if (autoSuggestionPopUpWindow.getParent() == null) {
            container.getLayeredPane().add(autoSuggestionPopUpWindow, JLayeredPane.POPUP_LAYER);
        }
        autoSuggestionPopUpWindow.setLocation(windowX, windowY);
        autoSuggestionPopUpWindow.setSelectedIndex(0);
        autoSuggestionPopUpWindow.setVisible(true);
    }

    public JList getSuggestionsPanel() {
        return autoSuggestionPopUpWindow;
    }

    public JTextComponent getTextField() {
        return textComp;
    }

    boolean doMemberShipSuggestion(String variable, String typedWord) {

        if (variable.isEmpty()) {
            return false;
        }

        boolean suggestionAdded = false;
        FuzzyVariable var = system.getFuzzyInputVariable(variable);
        if (var == null) {
            var = system.getFuzzyOutputVariable(variable);
        }
        if (var == null) {
            return false;
        }

        for (MemberShip ms : var.getMemberShips()) {
            String word = ms.getName();
            if (word.equalsIgnoreCase(typedWord)) {
                continue;
            }
            if (typedWord.isEmpty() || word.startsWith(typedWord)) {
                addWordToSuggestions(word.substring(typedWord.length()));
                suggestionAdded = true;
            }
        }
        return suggestionAdded;
    }

    public void windowGainedFocus(WindowEvent e) {
        textComp.requestFocusInWindow();
    }

    public void windowLostFocus(WindowEvent e) {
    }

    void insertCurrentSuggestion() {
        String wordToInsert = autoSuggestionPopUpWindow.getSelectedValue().toString();
        try {
            textComp.getDocument().insertString(textComp.getCaretPosition(), wordToInsert, null);
        } catch (BadLocationException ex) {
            Logger.getLogger(AutoSuggestor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void nextSuggestion() {
        int index = autoSuggestionPopUpWindow.getSelectedIndex();
        if (index < (this.suggestionListModel.getSize() - 1)) {
            ++index;
        } else {
            index = 0;
        }
        autoSuggestionPopUpWindow.setSelectedIndex(index);
    }

    public void previousSuggestion() {
        int index = autoSuggestionPopUpWindow.getSelectedIndex();
        if (index == 0) {
            index = this.suggestionListModel.getSize() - 1;
        } else {
            index--;
        }
        autoSuggestionPopUpWindow.setSelectedIndex(index);
    }
}
