package de.hszigr.mobileapps.questionnaire.client.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils.TruncateAt;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import de.hszigr.mobileapps.R;
import de.hszigr.mobileapps.questionnaire.client.model.Question;
import de.hszigr.mobileapps.questionnaire.client.model.QuestionType;
import de.hszigr.mobileapps.questionnaire.client.model.Questionnaire;
import de.hszigr.mobileapps.questionnaire.client.util.ActivityUtils;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionInputFactory;
import de.hszigr.mobileapps.questionnaire.client.util.QuestionnaireService;
import de.hszigr.mobileapps.questionnaire.client.util.Settings;
import de.hszigr.mobileapps.questionnaire.client.util.XmlUtils;

public class QuestionnaireActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	private LinearLayout layout;

	private Questionnaire questionnaire;

	private Map<String, View> viewMap = new HashMap<String, View>();

	private String baseUrl;
	private int currentQuestionNumber;

	private Uri uri;
	private String imageData;

	@Override
	public void onResume() {
		super.onResume();
		setContentView(R.layout.questionnaire);

		initialize();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(uri, projection, null, null, null);

				if (cursor.moveToFirst()) {
					int index = cursor
							.getColumnIndex(MediaStore.Images.Media.DATA);

					Bitmap image = BitmapFactory.decodeFile(cursor
							.getString(index));

					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					image.compress(CompressFormat.JPEG, 0, bos);

					imageData = Base64.encodeToString(bos.toByteArray(),
							Base64.DEFAULT);
				}
			}
		}
	}

	private void initialize() {
		findViewReferences();
		fetchQuestionnaire();
	}

	private void findViewReferences() {
		layout = (LinearLayout) findViewById(R.id.questionnaireLinearLayout);
	}

	private void fetchQuestionnaire() {
		retreiveBaseUrl();
		tryToRetreiveQuestionnaire();

		if (questionnaire != null) {
			resetCurrentQuestionNumber();
			buildQuestionnaireView();
		}
	}

	private void buildQuestionnaireView() {
		createQuestionViews();
		createSendButton();
	}

	private void resetCurrentQuestionNumber() {
		currentQuestionNumber = 1;
	}

	private void createSendButton() {
		Button button = new Button(this);
		button.setText(R.string.SEND);

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				sendData();
			}
		});

		layout.addView(button);
	}

	private void createQuestionViews() {
		for (Question question : questionnaire.getQuestions()) {
			View v = createViewFor(question);
			layout.addView(v);
		}
	}

	private void retreiveBaseUrl() {
		baseUrl = Settings.getBaseUrl(this);
	}

	private void tryToRetreiveQuestionnaire() {
		try {
			QuestionnaireService service = new QuestionnaireService();
			questionnaire = service.getQuestionnaire(baseUrl, getIntent()
					.getExtras().getString("qid"));

		} catch (IOException e) {
			ActivityUtils.showErrorMessage(this, e);
		}
	}

	private View createViewFor(Question question) {
		final String text = "" + currentQuestionNumber + ". "
				+ question.getText();
		currentQuestionNumber++;

		final LinearLayout questionLayout = createQuestionLayout();

		createQuestionTextView(text, questionLayout);

		createQuestionInput(question, questionLayout);

		viewMap.put(question.getId(), questionLayout);

		return questionLayout;
	}

	private void createQuestionInput(Question question,
			final LinearLayout questionLayout) {
		final View inputView = QuestionInputFactory.createViewFor(question,
				this);
		questionLayout.addView(inputView);

		if (question.getType() == QuestionType.ATTACHMENT) {
			LinearLayout layout = (LinearLayout) inputView;
			Button button = (Button) layout.getChildAt(0);
			button.setText(R.string.TAKE_PHOTO);

			button.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, "My Image");
					values.put(MediaStore.Images.Media.DESCRIPTION,
							"Image capture by camera");

					uri = getContentResolver().insert(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							values);

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

					startActivityForResult(intent,
							CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				}
			});
		}
	}

	private void createQuestionTextView(final String text,
			final LinearLayout questionLayout) {
		final TextView questionTextView = new TextView(this);
		questionTextView.setText(text);
		questionTextView.setEllipsize(TruncateAt.END);
		questionTextView.setHorizontallyScrolling(true);
		questionLayout.addView(questionTextView);
	}

	private LinearLayout createQuestionLayout() {
		final LinearLayout questionLayout = new LinearLayout(this);
		questionLayout.setOrientation(LinearLayout.VERTICAL);

		return questionLayout;
	}

	private void sendData() {
		Document doc = XmlUtils.createEmptyDocument();

		final Element answersElement = createAnswersElement(doc);
		createAnswerElements(doc, answersElement);

		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

		NetworkInfo wifi = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo mobile = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		if (!wifi.isAvailable() && !mobile.isAvailable()) {

		} else {
			final QuestionnaireService service = new QuestionnaireService();
			final Document validationResult = service.validate(baseUrl, doc);
			final Element validationElement = validationResult
					.getDocumentElement();
			final String status = validationElement.getAttribute("status");

			if ("success".equals(status)) {

				final Document sendResult = service.send(baseUrl, doc);

				final Element resultElement = sendResult.getDocumentElement();

				if ("success".equals(resultElement.getNodeName())) {
					ActivityUtils.showInfoMessage(this,
							R.string.THANKS_FOR_PARTICIPATING);
					finish();
				} else {
					ActivityUtils.showErrorMessage(this,
							R.string.ERROR_SENDING_DATA);
				}
			} else {
				final Element messageElement = (Element) validationElement
						.getElementsByTagName("message").item(0);

				final String error = messageElement.getAttribute("error");

				final Element inputElement = (Element) messageElement
						.getElementsByTagName("input").item(0);

				final String input = inputElement.getTextContent();

				if ("INVALID_ID".equals(error)) {
					ActivityUtils.showErrorMessage(this, R.string.INVALID_ID,
							input);
				} else if ("INVALID_QUESTION_ID".equals(error)) {
					ActivityUtils.showErrorMessage(this,
							R.string.INVALID_QUESTION_ID, input);
				} else if ("INVALID_TEXT".equals(error)) {
					ActivityUtils.showErrorMessage(this, R.string.INVALID_TEXT,
							input);
				} else if ("INVALID_LOCATION".equals(error)) {
					ActivityUtils.showErrorMessage(this,
							R.string.INVALID_LOCATION, input);
				} else if ("INVALID_CHOICE".equals(error)) {
					ActivityUtils.showErrorMessage(this,
							R.string.INVALID_CHOICE);
				}

			}
		}

	}

	private void createAnswerElements(Document doc, final Element answersElement) {
		for (Question question : questionnaire.getQuestions()) {
			final Element answerElement = createAnswerElement(doc,
					answersElement, question);

			final LinearLayout questionLayout = (LinearLayout) viewMap
					.get(question.getId());
			final LinearLayout layout = (LinearLayout) questionLayout
					.getChildAt(1);

			getAnswer(question, answerElement, layout);
		}
	}

	private void getAnswer(Question question, final Element answerElement,
			final LinearLayout layout) {
		switch (question.getType()) {
		case TEXT:
			getAnswerForText(answerElement, layout);
			break;

		case LOCATION:
			getAnswerForLocation(answerElement, layout);
			break;

		case CHOICE:
			getAnswerForChoice(answerElement, layout);
			break;

		case MULTICHOICE:
			getAnswerForMultiChoice(answerElement, layout);
			break;

		case ATTACHMENT:
			getAnswerForAttachment(answerElement, layout);
			break;
		}
	}

	private void getAnswerForMultiChoice(final Element answerElement,
			final LinearLayout layout) {
		String data = "";
		for (int i = 0; i < layout.getChildCount(); ++i) {
			CheckBox cb = (CheckBox) layout.getChildAt(i);

			if (cb.isChecked()) {
				data += (String) cb.getTag() + ",";
			}
		}

		if (data.endsWith(",")) {
			data = data.substring(0, data.length() - 1);
		}

		answerElement.setTextContent(data);
	}

	private void getAnswerForChoice(final Element answerElement,
			final LinearLayout layout) {
		RadioGroup radios = (RadioGroup) layout.getChildAt(0);

		for (int i = 0; i != radios.getChildCount(); ++i) {
			RadioButton rb = (RadioButton) radios.getChildAt(i);

			if (rb.isChecked()) {
				answerElement.setTextContent((String) rb.getTag());
				break;
			}
		}
	}

	private void getAnswerForLocation(final Element answerElement,
			final LinearLayout layout) {
		TextView text = (TextView) layout.getChildAt(0);
		answerElement.setTextContent(text.getText().toString());
	}

	private void getAnswerForText(final Element answerElement,
			final LinearLayout layout) {
		EditText edit = (EditText) layout.getChildAt(0);
		answerElement.setTextContent(edit.getText().toString());
	}

	private void getAnswerForAttachment(final Element answerElement,
			final LinearLayout layout) {
		answerElement.setTextContent(imageData);
	}

	private Element createAnswerElement(Document doc,
			final Element answersElement, Question question) {
		Element answerElement = doc.createElement("answer");
		answerElement.setAttribute("forQuestion", question.getId());
		answersElement.appendChild(answerElement);
		return answerElement;
	}

	private Element createAnswersElement(Document doc) {
		final Element answersElement = doc.createElement("answers");
		answersElement.setAttribute("forQuestionnaire", questionnaire.getId());
		doc.appendChild(answersElement);
		return answersElement;
	}
}
