package in.shamit.ml.nlp.corpus.sec.eddgar.time;

public class TimePeriod {
	int year;
	int quarter;
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getQuarter() {
		return quarter;
	}
	public void setQuarter(int quarter) {
		if(quarter < 1 || quarter > 4){
			throw new InvalidQuarterException("Invalid Quarter :: "+quarter);
		}
		this.quarter = quarter;
	}
	public TimePeriod(int year, int quarter) {
		super();
		setYear(year);
		setQuarter(quarter);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + quarter;
		result = prime * result + year;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TimePeriod))
			return false;
		TimePeriod other = (TimePeriod) obj;
		if (quarter != other.quarter)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimePeriod [year=" + year + ", quarter=" + quarter + "]";
	}	
}
