
package com.shopximity.document;

import java.io.Reader;

public interface DocumentSource
{
	public Reader open(String path)
		throws java.io.IOException;
}
