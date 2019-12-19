/*
	 * Description: This program is designed to construct a class with Relayparticipant objects that have nine parameters:
	 * firstName, lastName, teamName, admited, isCaptain, participant_index, sign_in_hour, sign_in_minute, and donationRaised;
	 * The class have built-in methods to check if the participant is admited to the event.
	 * Author: Geoffrey Qin
	 * Version: v1.0
	 * Date: May 15, 2018
	 */

public class RelayParticipant {
	
		//////////////////////////// variables ////////////////////////////

		protected String firstName;						//create a String to store the first name of the participant
		protected String lastName;						//create a String to store the last name of the participant
		String teamName;									//create a String to store the participant's team name
		boolean admited;									//create a boolean to store if the participant is permitted to the event or not
		boolean isCaptain;								//create a boolean to store if the participant is the captain of the team or not
		int participant_index;							//create a integer to store the index of the participant
		int sign_in_hour;								//create a integer to store the hour that the participant entered
		int sign_in_minute;								//create a integer to store the minutes that that participant entered
		double donationRaised;							//create a double to store the number of money the participant raised

		/////////////////////////// constructors ///////////////////////////

		RelayParticipant(String fn, String ln){	
			this.firstName = fn;							//sync the first name of the participant
			this.lastName = ln;							//sync the last name of the participant
			
		}//end constructor

		//////////////////////////////methods ///////////////////////////

		public String getTeam() {
			// input: participant name
			// output: team name
			return this.teamName;						//return the name of the team that the participant enrolled in
		}//end get Team
		
		
		public double getDonation() {
			// input: participant name
			// output: donationRaised
			return this.donationRaised;					//return the donation the participant has raised
		}//end getDonation
		
		public boolean printAdmited() {
			// input: participant name
			// output: admited
			return this.admited;							//return the admited of the participant
		}//end printAdmited
		
		public void checkAdmission() {
			// input: participant name
			// output: admited
			if(this.donationRaised >= 50) {				//check if the participant has rasied over $50
				this.admited = true;						//set the admited to true if participant has raised $50
			}else {
				this.admited = false;					//set the admited to false if participant has not raised $50
			}//end else if(this.donationRaised >= 50)
		}//end method

}//end class
