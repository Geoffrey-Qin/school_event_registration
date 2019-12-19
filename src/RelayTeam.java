
/*
	 * Description: This program is designed to construct a class with RelayTeam objects that have four parameters:
	 * teamName, captainName, teamDonation and size; The class have built-in methods to calculate the total donation of the team.
	 * Author: Geoffrey Qin
	 * Version: v1.0
	 * Date: May 16, 2018
	 */

public class RelayTeam {

	//////////////////////////// variables ////////////////////////////

	String teamName;											//create a String to store the name of the team
	String captainName;										//create a String to store the captain name of the team
	double teamDonation;										//create a double to store the total donation of the team
	int size;												//create an integer to store the number of people in the team


	/////////////////////////// constructors ///////////////////////////

	RelayTeam(String tn){
		this.teamName = tn;									//sync the name of the team

	}//end constructor

	//////////////////////////////methods ///////////////////////////

	public String getCaptain() {
		// input: team name
		// output: captain name
		return this.captainName;								//return the name of the team that the participant enrolled in
	}//end getCaptain

	public double getTeamDonation(int i, double tmd[]) {
		//Recursive method to calculate the total donation of the team
		//Input: the size of the team, and the array of donations raised by the members
		//Output: Return the total donation of the team
		if ( i == 0 ) {										//check if the program has reached 0 yet
			return tmd[i];									//return the first participant donation
		}else {
			return (tmd[i] + getTeamDonation(i - 1, tmd) ) ;	//calculate the total donation of the team
		}//end else
	}//end method getTeamDonation 

}//end class
