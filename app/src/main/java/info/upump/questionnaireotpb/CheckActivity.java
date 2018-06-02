package info.upump.questionnaireotpb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.upump.questionnaireotpb.db.QuestionDAO;
import info.upump.questionnaireotpb.entity.Answer;
import info.upump.questionnaireotpb.entity.Question;

public class CheckActivity extends AppCompatActivity {

    private TextView goodAnswerText, badAnswerText, questionText;
    private RadioGroup answersGroup;
    private List<Question> questionList = new ArrayList<>();
    private List<Answer> currentAnswerList = new ArrayList<>();
    private int number;
    private int good;
    private int bad;
    private String category;
    private static final String CATEGORY = "cat";

    public static Intent createIntent(Context context, String category) {
        Intent intent = new Intent(context, CheckActivity.class);
        intent.putExtra(CATEGORY, category);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        setTitle(getString(R.string.title_check_acrivity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        goodAnswerText = findViewById(R.id.check_activity_good_text_view);
        badAnswerText = findViewById(R.id.check_activity_bad_text_view);
        questionText = findViewById(R.id.check_activity_question_text_view);
        answersGroup = findViewById(R.id.check_activity_answers_group);

        answersGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println(checkedId);
                checkAnswer(checkedId);
            }
        });

        category = getIntent().getStringExtra(CATEGORY);

        initQuestions();
        start();
    }

    private void checkAnswer(int checkedId) {
        Answer answer = currentAnswerList.get(checkedId);
        String s;
        if (answer.getRight() == 1) {
            goodAnswerText.setText(String.valueOf(++good));
            s = getString(R.string.toast_right);
        } else {
            badAnswerText.setText(String.valueOf(++bad));
            s = getString(R.string.toast_false);
        }
        Toast.makeText(this, s,Toast.LENGTH_SHORT ).show();
        if (number < questionList.size() - 1) {
            start();
        } else startResult();

    }

    private void startResult() {
        final String title = getString(R.string.title_result);
        String message = goodAnswerText.getText() + getString(R.string.title_count_result);
        String button1String = getString(R.string.title_finish);
        String button2String = getString(R.string.title_restart);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение
        builder.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                exit();
            }
        });
        builder.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                reset();
                start();
            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void reset() {
        initQuestions();
    }

    private void start() {
        number++;
        Question question = questionList.get(number);
        questionText.setText(question.getBody());
        setImage();
        currentAnswerList = question.getAnswers();
        answersGroup.removeAllViews();
        int id = 0;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 15, 0, 0);
        for (Answer a : currentAnswerList) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setLayoutParams(lp);
            radioButton.setText(a.getBody());
            radioButton.setPadding(4, 4, 4, 4);
            radioButton.setTextColor(getResources().getColor(R.color.cardview_dark_background));
            radioButton.setId(id);
            answersGroup.addView(radioButton);
            id++;
        }
    }

    private void setImage() {

    }

    private void initQuestions() {
        good = 0;
        bad = 0;
        number = -1;
        badAnswerText.setText(String.valueOf(bad));
        goodAnswerText.setText(String.valueOf(good));
        QuestionDAO questionDAO = new QuestionDAO(this);
        questionList = questionDAO.getQuestions(category);
    }

    private void exit() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            exit();
        }
        return super.onOptionsItemSelected(item);
    }
}
