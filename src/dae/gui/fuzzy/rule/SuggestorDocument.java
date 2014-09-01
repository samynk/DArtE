/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dae.gui.fuzzy.rule;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

public class SuggestorDocument extends DefaultStyledDocument {
    
    private AutoSuggestor suggestor;
    
    public SuggestorDocument(AutoSuggestor suggestor) {
        this.suggestor = suggestor;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str.equals("\n") || str.equals("\r")) {
            if (suggestor.getSuggestionsPanel().isVisible()) {
                suggestor.insertCurrentSuggestion();
                suggestor.getSuggestionsPanel().setVisible(false);
                return;
            }
        }        
        super.insertString(offs, str, a);
    }
}
