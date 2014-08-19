
public class Review {

	
	int ID;
	String Author;
	String Date;
	String ProductID;
	String RunningNumber;
	String Title;
	String Content;
	String Stars;
	String Ratio;
	String ProductName;
	
	
	public void setAuthor(String author) {
		Author = author;
		
	}

	public void setDate(String date) {
		Date = date;
		
	}

	public void setProductID(String productID) {
		ProductID = productID;
		
	}

	public void setRunningNumber(String runningNumber) {
		RunningNumber = runningNumber;
		
	}

	public void setTitle(String title) {
		Title = title;
		
	}

	public void setStars(String stars) {
		Stars = stars;
		
	}

	public void setRatio(String ratio) {
		Ratio = ratio;
		
	}

	public void setContent(String content) {
		Content = content;		
	}
	
	public String getContent()
	{
		return Content;
	}

	public void setProductName(String name) {
		ProductName = name;
		
	}

	public void setID(int id) {
		ID = id;	
	}
	
	public int getID()
	{
		return ID;
	}

}
