package fileio;

import gameboard.Robot;
import java.util.Queue;
import java.util.Scanner;

/**
 *
 * @author short
 */
public class ChallengeFile {

    private String winCondition, fileName;
    private Scanner content;
    //private Queue<Robot> robotQueue;
    private String[] caseCondition, tapes;
    private int cases;

    public void setFileName(String fileName) {
	this.fileName = fileName;
    }

    public String getFileName() {
	return fileName;
    }

    public String getWinCondition() {
	return winCondition;
    }

//    public Scanner getContent() {
//	return content;
//    }
    public int getCases() {
	return cases;
    }

    public void setWinCondition(String winCondition) {
	this.winCondition = winCondition;
    }

    public void setContent(Scanner content) {
	//this.content = content;
	caseCondition = new String[cases];
	tapes = new String[cases];
	for (int i = 0; i < cases; i++) //TODO: build queue of robots with 1 tape each
	{
	    caseCondition[i] = content.nextLine();
	    tapes[i] = content.nextLine();
	    System.out.printf("Case: %s\nTape: %s\n", caseCondition[i], tapes[i]);
	}
    }

    public String[] getCaseCondition() {
	return caseCondition;
    }

    public String[] getTapes() {
	return tapes;
    }

    public void setCases(int cases) {
	this.cases = cases;
    }

}
