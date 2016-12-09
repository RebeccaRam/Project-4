
public class Page2 {
	//represents a page in memory
	//page object will be represented as an array of characters
	public byte [] pgData;
	
	public byte getData( int offset) {
		//takes in a page offset and returns the char in the array
		return pgData[offset];
	}

}
