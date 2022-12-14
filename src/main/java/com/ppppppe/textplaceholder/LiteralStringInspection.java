package com.ppppppe.textplaceholder;

import com.intellij.codeInspection.AbstractBaseJavaLocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.*;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;


public class LiteralStringInspection extends AbstractBaseJavaLocalInspectionTool {

    // Defines the text of the quick fix intention
    public static final String QUICK_FIX_NAME = "Generate placeholder text with the same length as the string to be replaced";
    private static final Logger LOG = Logger.getInstance("#com.example.demo.ComparingReferencesInspection");
    private final CriQuickFix myQuickFix = new CriQuickFix();

    public String someText = "someText";

    /**
     * This method is called to get the panel describing the inspection.
     * It is called every time the user selects the inspection in preferences.
     *
     * @return panel to display inspection information.
     */
    @Override
    public JComponent createOptionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        final JTextField textField = new JTextField(someText);
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            public void textChanged(@NotNull DocumentEvent event) {
                someText = textField.getText();
            }
        });
        panel.add(textField);
        return panel;
    }

    /**
     * This method is overridden to provide a custom visitor.
     * The visitor must not be recursive and must be thread-safe.
     *
     * @param holder     object for visitor to register problems found.
     * @param isOnTheFly true if inspection was run in non-batch mode
     * @return non-null visitor for this inspection.
     * @see JavaElementVisitor
     */
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new JavaElementVisitor() {

            public void visitDeclarationStatement(PsiDeclarationStatement statement) {
                visitStatement(statement);
            }

            public void visitLiteralExpression(PsiLiteralExpression expression) {

                visitExpression(expression);
                PsiType type = expression.getType();
                PsiElement parent = expression.getParent();

                // PsiNameValuePair

                if ((type != null) &&
                        (type.equalsToText("java.lang.String")) &&
                        !(parent instanceof PsiArrayInitializerMemberValue) &&
                        !(parent instanceof PsiNameValuePair)
                ) {
                    holder.registerProblem(expression, DESCRIPTION_TEMPLATE, myQuickFix);
                }
            }


            /**
             * This string defines the short message shown to a user signaling the inspection found a problem.
             * It reuses a string from the inspections bundle.
             */
            @NonNls
            private final String DESCRIPTION_TEMPLATE = "Substitute placeholder text";

        };
    }

    private static class CriQuickFix implements LocalQuickFix {

        @NotNull
        @Override
        public String getName() {
            return QUICK_FIX_NAME;
        }

        private PlaceHolder holder = new PlaceHolder(PlaceHolder.Mode.REMOTE);


        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            try {
                PsiLiteralExpression literal = (PsiLiteralExpression) descriptor.getPsiElement();

                String value = (String)(literal.getValue());
                PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
                if ((!holder.isRemoteAvailable()) && (!holder.popupIsShown)) {
                    holder.popupIsShown = true;
                    unavailableResourceWarning(project, holder);
                }
                String strExpression = "\"" + holder.generatePlaceholder(value.length()) + "\"";
                PsiElement strLiteral = factory.createExpressionFromText(strExpression, null);
                literal.replace(strLiteral);
            } catch (IncorrectOperationException e) {
                LOG.error(e);
            }
        }

        private void unavailableResourceWarning(@NotNull Project project, PlaceHolder holder) {
            // .createMessage("Connection failure: Bacon resource unavailable");
            StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
            JBPopupFactory.getInstance()
                    .createConfirmation(
                            "Connection failure: Bacon resource unavailable",
                            "Use local placeholder",
                            "Do nothing",
                            () -> {holder.defaultPlaceHolder = "Some action " + holder.defaultPlaceHolder;},
                            0
                    ).showInFocusCenter();
        }


        @NotNull
        public String getFamilyName() {
            return getName();
        }

    }

    @SuppressWarnings({"unchecked", "deprecation"})
    private static class SomeClass {
        int e = 10;
        String f = "Pig nulla aliquip est prosciutto ground round eiusmod. Lorem kielbasa ut officia buffalo ";
        String s = "nulla venenatis tincidunt non nec justo. Sed ut rutrum arcu, et suscipit quam Some action Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut ut velit nulla. Nunc vulputate eleifend dapibus. Phasellus vestibulum dui quis scelerisque hendrerit. Vestibulum scelerisque consequat viverra. Donec dictum velit eget erat sagittis, vitae vestibulum urna congue. Integer lacus risus, tristique eu diam ac, venenatis fringilla nisi. Aliquam non felis ";
    }

}
