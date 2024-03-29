package org.eclipse.gemoc.protocols.eaop.api.messages;


import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.jsonrpc.messages.ResponseMessage;
import org.eclipse.lsp4j.jsonrpc.validation.NonNull;


/**
 * EAOP specific version of ResponseMessage.
 *
 * @see EaopMessageTypeAdapter
 */
public class EaopResponseMessage extends ResponseMessage {
	/**
	 * The response id.
	 *
	 * The {@link #getId()} field is the id of the message being replied to.
	 */
	@NonNull
	private Either<String, Number> responseId;

	public String getResponseId() {
		if (responseId == null)
			return null;
		if (responseId.isLeft())
			return responseId.getLeft();
		if (responseId.isRight())
			return responseId.getRight().toString();
		return null;
	}
	
	@NonNull
	public Either<String, Number> getRawResponseId() {
		return responseId;
	}

	public void setResponseId(String id) {
		this.responseId = Either.forLeft(id);
	}
	
	public void setResponseId(int id) {
		this.responseId = Either.forRight(id);
	}
	
	public void setRawResponseId(@NonNull Either<String, Number> id) {
		this.responseId = id;
	}

	/**
	 * The method that was invoked.
	 */
	@NonNull
	private String method;

	@NonNull
	public String getMethod() {
		return this.method;
	}

	public void setMethod(@NonNull String method) {
		this.method = method;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		if (!super.equals(obj))
			return false;
		EaopResponseMessage other = (EaopResponseMessage) obj;
		if (this.responseId == null) {
			if (other.responseId != null)
				return false;
		} else if (!this.responseId.equals(other.responseId))
			return false;
		if (this.method == null) {
			if (other.method != null)
				return false;
		} else if (!this.method.equals(other.method))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((this.responseId == null) ? 0 : this.responseId.hashCode());
		result = prime * result + ((this.method == null) ? 0 : this.method.hashCode());
		return result;
	}
}
