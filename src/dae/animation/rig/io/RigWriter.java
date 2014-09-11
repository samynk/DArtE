/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.animation.rig.io;

import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;
import dae.animation.rig.AnimationController;
import dae.animation.rig.AnimationListControl;
import dae.animation.rig.Rig;
import dae.animation.skeleton.BodyElement;
import dae.io.SceneSaver;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mlproject.fuzzy.FuzzyRule;
import mlproject.fuzzy.FuzzyRuleBlock;
import mlproject.fuzzy.FuzzySystem;
import mlproject.fuzzy.FuzzyVariable;
import mlproject.fuzzy.LeftSigmoidMemberShip;
import mlproject.fuzzy.MemberShip;
import mlproject.fuzzy.RightSigmoidMemberShip;
import mlproject.fuzzy.SigmoidMemberShip;
import mlproject.fuzzy.SingletonMemberShip;
import mlproject.fuzzy.TrapezoidMemberShip;

/**
 *
 * @author Koen Samyn
 */
public class RigWriter {

    /**
     * Write the scene to a file.
     *
     * @param location The location of the file.
     * @param body the body to write to file.
     */
    public static void writeRig(File location, Rig rig) {
        FileWriter fw;
        BufferedWriter bw = null;
        try {
            if (!location.getParentFile().exists()) {
                location.getParentFile().mkdirs();
            }
            fw = new FileWriter(location);

            bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<rig>\n");
            int depth = 0;
            for (Spatial child : rig.getChildren()) {
                if (child instanceof BodyElement) {
                    BodyElement be = (BodyElement) child;
                    be.write(bw, depth + 1);
                }
            }
            bw.write("\t<fuzzysystems>\n");
            writeFuzzySystem(bw, rig.getFuzzySystem());
            bw.write("\t</fuzzysystems>\n");
            // animation targets
            bw.write("\t<animationtargets>\n");
            for (int i = 0; i < rig.getNrOfTargetKeys(); ++i) {
                String key = rig.getTargetKeyAt(i);
                Prefab value = rig.getTarget(key);
                bw.write("\t\t<target ");
                XMLUtils.writeAttribute(bw, "key", key);
                if (value != null) {
                    XMLUtils.writeAttribute(bw, "target", value.getName());
                }
                bw.write("/>\n");
            }
            bw.write("\t</animationtargets>\n");
            // controller connections
            AnimationListControl alc = rig.getControl(AnimationListControl.class);
            if ( alc != null )
            {
                bw.write("\t<controllerconnections>\n");
                for ( AnimationController ac: alc.getAnimationControllers()){
                    bw.write("\t\t<controller ");
                    XMLUtils.writeAttribute(bw, "system", ac.getSystemName());
                    XMLUtils.writeAttribute(bw, "name", ac.getName());
                    XMLUtils.writeAttribute(bw, "inputToSystem", ac.getControllerInputName());
                    XMLUtils.writeAttribute(bw, "outputOfSystem", ac.getControllerOutputName());
                    bw.write(">\n");
                    bw.write("\t\t\t");
                    bw.write( ac.getInput().toXML());
                    bw.write("\t\t\t");
                    bw.write(ac.getOutput().toXML());
                    bw.write("\t\t</controller>\n");
                            
                }
                bw.write("\t</controllerconnections>\n");
            }
            bw.write("</rig>\n");
        } catch (IOException ex) {
            Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger("DArtE").log(Level.SEVERE, null, ex);
            }
        }
    }

    private static void writeFuzzySystem(BufferedWriter bw, FuzzySystem fuzzySystem) throws IOException {
        bw.write("\t\t<fuzzysystem ");
        XMLUtils.writeAttribute(bw, "name", fuzzySystem.getName());
        bw.write(">\n");
        bw.write("\t\t\t<fuzzyinputs>\n");
        for (FuzzyVariable fuzzyVariable : fuzzySystem.getInputs()) {
            bw.write("\t\t\t\t<input ");
            XMLUtils.writeAttribute(bw, "name", fuzzyVariable.getName());
            bw.write(">\n");
            for (MemberShip ms : fuzzyVariable.getMemberShips()) {
                if (ms instanceof LeftSigmoidMemberShip) {
                    writeMemberShip(bw, (LeftSigmoidMemberShip) ms);
                } else if (ms instanceof RightSigmoidMemberShip) {
                    writeMemberShip(bw, (RightSigmoidMemberShip) ms);
                } else if (ms instanceof SigmoidMemberShip) {
                    writeMemberShip(bw, (SigmoidMemberShip) ms);
                } else if (ms instanceof TrapezoidMemberShip) {
                    writeMemberShip(bw, (TrapezoidMemberShip) ms);
                }
            }
            bw.write("\t\t\t\t</input>\n");
        }
        bw.write("\t\t\t</fuzzyinputs>\n");

        bw.write("\t\t\t<fuzzyoutputs>\n");
        for (FuzzyVariable fuzzyVariable : fuzzySystem.getOutputs()) {
            bw.write("\t\t\t\t<output ");
            XMLUtils.writeAttribute(bw, "name", fuzzyVariable.getName());
            bw.write(">\n");
            for (MemberShip ms : fuzzyVariable.getMemberShips()) {
                if (ms instanceof LeftSigmoidMemberShip) {
                    writeMemberShip(bw, (LeftSigmoidMemberShip) ms);
                } else if (ms instanceof RightSigmoidMemberShip) {
                    writeMemberShip(bw, (RightSigmoidMemberShip) ms);
                } else if (ms instanceof SigmoidMemberShip) {
                    writeMemberShip(bw, (SigmoidMemberShip) ms);
                } else if (ms instanceof TrapezoidMemberShip) {
                    writeMemberShip(bw, (TrapezoidMemberShip) ms);
                } else if (ms instanceof SingletonMemberShip) {
                    writeMemberShip(bw, (SingletonMemberShip) ms);
                }
            }
            bw.write("\t\t\t\t</output>\n");
        }
        bw.write("\t\t\t</fuzzyoutputs>\n");
        bw.write("\t\t\t<fuzzyrules>\n");
        for (FuzzyRuleBlock ruleBlock : fuzzySystem.getFuzzyRuleBlocks()) {
            if (ruleBlock == null) {
                continue;
            }
            bw.write("\t\t\t\t<ruleblock ");
            XMLUtils.writeAttribute(bw, "name", ruleBlock.getName());
            bw.write(">\n");
            for (FuzzyRule rule : ruleBlock.getRules()) {
                bw.write("\t\t\t\t\t<rule>");
                bw.write(rule.getRuleText());
                bw.write("</rule>\n");
            }
            bw.write("\t\t\t\t</ruleblock>\n");
        }
        bw.write("\t\t\t</fuzzyrules>\n");
        bw.write("\t\t</fuzzysystem>\n");
    }

    private static void writeMemberShip(BufferedWriter bw, LeftSigmoidMemberShip lms) throws IOException {
        bw.write("\t\t\t\t\t<membership ");
        XMLUtils.writeAttribute(bw, "type", "left");
        XMLUtils.writeAttribute(bw, "name", lms.getName());
        XMLUtils.writeAttribute(bw, "center", lms.getCenter());
        XMLUtils.writeAttribute(bw, "right", lms.getRight());
        bw.write("/>\n");
    }

    private static void writeMemberShip(BufferedWriter bw, RightSigmoidMemberShip rms) throws IOException {
        bw.write("\t\t\t\t\t<membership ");
        XMLUtils.writeAttribute(bw, "type", "right");
        XMLUtils.writeAttribute(bw, "name", rms.getName());
        XMLUtils.writeAttribute(bw, "center", rms.getCenter());
        XMLUtils.writeAttribute(bw, "left", rms.getLeft());
        bw.write("/>\n");
    }

    private static void writeMemberShip(BufferedWriter bw, SigmoidMemberShip sms) throws IOException {
        bw.write("\t\t\t\t\t<membership ");
        XMLUtils.writeAttribute(bw, "type", "triangular");
        XMLUtils.writeAttribute(bw, "name", sms.getName());
        XMLUtils.writeAttribute(bw, "left", sms.getLeft());
        XMLUtils.writeAttribute(bw, "center", sms.getCenter());
        XMLUtils.writeAttribute(bw, "right", sms.getRight());
        bw.write("/>\n");
    }

    private static void writeMemberShip(BufferedWriter bw, TrapezoidMemberShip sms) throws IOException {
        bw.write("\t\t\t\t\t<membership ");
        XMLUtils.writeAttribute(bw, "type", "trapezoid");
        XMLUtils.writeAttribute(bw, "name", sms.getName());
        XMLUtils.writeAttribute(bw, "left", sms.getLeft());
        XMLUtils.writeAttribute(bw, "centerleft", sms.getCenterLeft());
        XMLUtils.writeAttribute(bw, "centerright", sms.getCenterRight());
        XMLUtils.writeAttribute(bw, "right", sms.getRight());
        bw.write("/>\n");
    }

    private static void writeMemberShip(BufferedWriter bw, SingletonMemberShip sms) throws IOException {
        bw.write("\t\t\t\t\t<membership ");
        XMLUtils.writeAttribute(bw, "type", "singleton");
        XMLUtils.writeAttribute(bw, "name", sms.getName());
        XMLUtils.writeAttribute(bw, "center", sms.getValue());
        bw.write("/>\n");
    }
}
