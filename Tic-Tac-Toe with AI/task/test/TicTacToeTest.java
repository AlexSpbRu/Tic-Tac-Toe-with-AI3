import org.hyperskill.hstest.v6.stage.BaseStageTest;
import org.hyperskill.hstest.v6.testcase.CheckResult;
import org.hyperskill.hstest.v6.testcase.TestCase;
import tictactoe.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

enum FieldState {
    X, O, FREE;

    static FieldState get(char symbol) {
        switch (symbol) {
            case 'X': return X;
            case 'O': return O;
            case ' ': return FREE;
            default: return null;
        }
    }
}

class TicTacToeField {

    final FieldState[][] field;

    TicTacToeField(FieldState[][] field) {
        this.field = new FieldState[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                this.field[row][col] = field[row][col];
            }
        }
    }

    TicTacToeField(String str) {
        field = new FieldState[3][3];

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                field[row][col] =
                    FieldState.get(str.charAt(((2 - row) * 3 + col)));
            }
        }
    }

    boolean equalTo(TicTacToeField other) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] != other.field[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean hasNextAs(TicTacToeField other) {
        boolean improved = false;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j] != other.field[i][j]) {
                    if (field[i][j] == FieldState.FREE && !improved) {
                        improved = true;
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        return improved;
    }

    boolean isCloseTo(TicTacToeField other) {
        return equalTo(other)
            || hasNextAs(other)
            || other.hasNextAs(this);
    }

    static TicTacToeField parse(String fieldStr) {

        try {
            List<String> lines = fieldStr
                .lines()
                .map(String::strip)
                .filter(e ->
                    e.startsWith("|")
                        && e.endsWith("|"))
                .collect(Collectors.toList());

            for (String line : lines) {
                for (char c : line.toCharArray()) {
                    if (c != 'X'
                        && c != 'O'
                        && c != '|'
                        && c != ' '
                        && c != '_') {
                        return null;
                    }
                }
            }

            FieldState[][] field = new FieldState[3][3];

            int y = 2;
            for (String line : lines) {
                char[] cols = new char[] {
                    line.charAt(2),
                    line.charAt(4),
                    line.charAt(6)
                };

                int x = 0;
                for (char c : cols) {
                    FieldState state = FieldState.get(c);
                    if (state == null) {
                        return null;
                    }
                    field[y][x] = state;
                    x++;
                }
                y--;
            }

            return new TicTacToeField(field);
        } catch (Exception ex) {
            return null;
        }
    }

    static List<TicTacToeField> parseAll(String output) {
        List<TicTacToeField> fields = new ArrayList<>();

        List<String> lines = output
            .lines()
            .map(String::strip)
            .filter(e -> e.length() > 0)
            .collect(Collectors.toList());

        String candidateField = "";
        boolean insideField = false;
        for (String line : lines) {
            if (line.contains("----") && !insideField) {
                insideField = true;
                candidateField = "";
            } else if (line.contains("----") && insideField) {
                TicTacToeField field = TicTacToeField.parse(candidateField);
                if (field != null) {
                    fields.add(field);
                }
                insideField = false;
            }

            if (insideField && line.startsWith("|")) {
                candidateField += line + "\n";
            }
        }

        return fields;
    }

}


class Clue {
    int x, y;
    Clue(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class TicTacToeTest extends BaseStageTest<Clue> {
    public TicTacToeTest() throws Exception {
        super(Main.class);
    }

    static String[] inputs = new String[] {
        "1 1", "1 2", "1 3",
        "2 1", "2 2", "2 3",
        "3 1", "3 2", "3 3"
    };

    String iterateCells(String initial) {
        int index = -1;
        for (int i = 0; i < inputs.length; i++) {
            if (initial.equals(inputs[i])) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return "";
        }
        String fullInput = "";
        for (int i = index; i < index + 9; i++) {
            fullInput += inputs[i % inputs.length] + "\n";
        }
        return fullInput;
    }

    @Override
    public List<TestCase<Clue>> generate() {

        List<TestCase<Clue>> tests = new ArrayList<>();

        int i = 0;
        for (String input : inputs) {
            String fullMoveInput = iterateCells(input);

            String[] strNums = input.split(" ");
            int x = Integer.parseInt(strNums[0]);
            int y = Integer.parseInt(strNums[1]);

            if (i % 2 == 1) {
                // mix with incorrect data
                fullMoveInput = "4 " + i + "\n" + fullMoveInput;
            }

            String fullGameInput = "";
            for (int j = 0; j < 9; j++) {
                fullGameInput += fullMoveInput;
            }

            String initial;

            switch (i % 6) {
                case 0: initial = "start user easy\n"; break;
                case 1: initial = "start easy user\n"; break;
                case 2: initial = "start user medium\n"; break;
                case 3: initial = "start medium user\n"; break;
                case 4: initial = "start user hard\n"; break;
                case 5: initial = "start hard user\n"; break;
                default: continue;
            }

            fullGameInput = initial + fullGameInput + "exit";

            tests.add(new TestCase<Clue>()
                .setInput(fullGameInput));

            i++;
        }

        tests.add(new TestCase<Clue>()
            .setInput("start easy easy\nexit"));

        tests.add(new TestCase<Clue>()
            .setInput("start medium medium\nexit"));

        tests.add(new TestCase<Clue>()
            .setInput("start hard hard\nexit"));


        tests.add(new TestCase<Clue>()
            .setInput("start medium easy\nexit"));

        tests.add(new TestCase<Clue>()
            .setInput("start easy medium\nexit"));

        tests.add(new TestCase<Clue>()
            .setInput("start medium hard\nexit"));

        tests.add(new TestCase<Clue>()
            .setInput("start hard medium\nexit"));

        tests.add(new TestCase<Clue>()
            .setInput("start easy hard\nexit"));

        tests.add(new TestCase<Clue>()
            .setInput("start hard easy\nexit"));


        tests.add(new TestCase<Clue>()
            .setInput("start user user\n" +
                "1 1\n" +
                "2 2\n" +
                "1 2\n" +
                "2 1\n" +
                "1 3\n" +
                "exit"));

        return tests;
    }

    @Override
    public CheckResult check(String reply, Clue clue) {

        List<TicTacToeField> fields = TicTacToeField.parseAll(reply);

        if (fields.size() == 0) {
            return new CheckResult(false, "No fields found");
        }

        for (int i = 1; i < fields.size(); i++) {
            TicTacToeField curr = fields.get(i - 1);
            TicTacToeField next = fields.get(i);

            if (!(curr.equalTo(next) || curr.hasNextAs(next))) {
                return new CheckResult(false,
                    "For two fields following each " +
                        "other one is not a continuation " +
                        "of the other (they differ more than in two places).");
            }
        }

        return CheckResult.TRUE;
    }
}
