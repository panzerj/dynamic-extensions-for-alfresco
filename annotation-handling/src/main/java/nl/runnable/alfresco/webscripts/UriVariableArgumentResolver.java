/*
Copyright (c) 2012, Runnable
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of Runnable nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package nl.runnable.alfresco.webscripts;

import java.lang.annotation.Annotation;

import nl.runnable.alfresco.webscripts.annotations.UriVariable;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class UriVariableArgumentResolver implements ArgumentResolver<Object, UriVariable> {

	/* Dependencies */

	private final StringValueConverter stringValueConverter;

	/* Main operations */

	UriVariableArgumentResolver(final StringValueConverter stringValueConverter) {
		Assert.notNull(stringValueConverter);
		this.stringValueConverter = stringValueConverter;
	}

	@Override
	public boolean supports(final Class<?> parameterType, final Class<? extends Annotation> annotationType) {
		return getStringValueConverter().isSupportedType(parameterType) && UriVariable.class.equals(annotationType);
	}

	@Override
	public Object resolveArgument(final Class<?> parameterType, final UriVariable uriVariable, final String name,
			final WebScriptRequest request, final WebScriptResponse response) {
		String variableName = uriVariable.value();
		if (StringUtils.hasText(variableName) == false) {
			variableName = name;
		}
		if (StringUtils.hasText(variableName) == false) {
			throw new RuntimeException(
					"Cannot determine name of URI variable. Specify the name using the @UriVariable annotation.");
		}
		final String variable = request.getServiceMatch().getTemplateVars().get(variableName);
		if (variable == null && uriVariable.required()) {
			throw new IllegalStateException(String.format("URI variable not available: %s", variableName));
		}
		return getStringValueConverter().convertStringValue(parameterType, variable);
	}

	/* Dependencies */

	public StringValueConverter getStringValueConverter() {
		return stringValueConverter;
	}
}
