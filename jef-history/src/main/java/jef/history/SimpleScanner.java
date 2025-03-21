package jef.history;

public class SimpleScanner
{
	private String text;

	public SimpleScanner(final String text)
	{
		this.text = text;
	}

	public String extract(final String startSequence, final String endSequence)
	{
		return this.extract(startSequence, endSequence, true);
	}

	public String extract(final String startSequence, final String endSequence, final boolean includeEnd)
	{
		var startIndex = 0;
		if (startSequence != null)
		{
			startIndex = this.text.indexOf(startSequence);
			if (startIndex == -1)
				return null;
		}

		final var endIndex = this.text.indexOf(endSequence, startIndex + startSequence.length());

		final var ret = this.text.substring(startIndex, endIndex + (includeEnd ? endSequence.length() : 0));

		this.text = this.text.substring(endIndex + (includeEnd ? endSequence.length() : 0));

//		System.out.println(ret.substring(0, 20));
		return ret;
	}

	public String extractBetween(final String startSequence, final String endSequence)
	{
		return this.extract(startSequence, endSequence, false).substring(startSequence.length());
	}

	public String getText()
	{
		return this.text;
	}

	public String scanAhead(final int count)
	{
		final var ret = this.text.substring(0, count);
		this.text = this.text.substring(count);
		return ret;
	}

	public String scanTo(final String sequence)
	{
		return this.scanTo(sequence, false);
	}

	public String scanTo(final String sequence, final boolean includeSequence)
	{
		var startIndex = 0;
		if (sequence != null)
		{
			startIndex = this.text.indexOf(sequence);
			if (startIndex == -1)
				return null;
		}

		final var ret = this.text.substring(0, startIndex + (includeSequence ? sequence.length() : 0));
		this.text = this.text.substring(startIndex + (includeSequence ? sequence.length() : 0));

		return ret;
	}

}
