package tictactoe;

import java.util.*;

abstract class Player {
    protected char simbol;
    Player( char simb ) {
        simbol = simb;

    }
    abstract  void makeMove(  );
    abstract  void moving(  );
}

class AIPlayerSimple extends Player {
    AIPlayerSimple( char simb ) {
        super(simb);
        //System.out.println("Create AI");
    }
    @Override
    void makeMove(  )  {
        int row = (int)(Math.random()*2);
        int startRow = row;
        //System.out.println("startRow : " + startRow + "  Row : " + row);
        boolean stop = false;
        while(!stop) {
            for( int i = 0 ; i < 3 ; ++i ) {
                char ch = Main.GetStateAI( i, row );
                if(ch != 'X' && ch != 'O') {
                    //System.out.println("SetState  row : " + row + "  col : " + i);
                    Main.SetStateAI( i , row, simbol );
                    stop = true;
                    break;
                }
            }
            row++;
            if( row > 3 )
                row = 1;
            if( row == startRow)
                break;
        }


    }
    @Override
    void moving(  ) {
        makeMove(  );
        System.out.println("Making move level \"easy\"");
    }
}

class AIPlayerMedium extends AIPlayerSimple {
    AIPlayerMedium( char simb ) {
        super(simb);
        //System.out.println("Create AI");
    }

    boolean checkAndSet( char simb ) {
        int resV = 0, resH = 0, resDiag1 = 0, resDiag2 = 0;
        int rD1 = -1, rD2 = -1, resD1 = -1, resD2 = -1;

        for( int i = 0 ; i < 3 ;  ++i ) {
            resV = 0;
            resH = 0;
            int numSpaceH = -1;
            int numSpaceV = -1;
            int rH = -1;
            int rV = -1;
            for (int j = 0; j < 3; ++j) {
                rV = Main.GetRes(i, j, simb);
                rH = Main.GetRes(j, i, simb);
                resV += rV;
                resH += rH;
                if (rH == 0) numSpaceH = j;
                if (rV == 0) numSpaceV = j;
            }

            if (resV == 2) {
                Main.SetStateAI( i, numSpaceV, simbol);
                return true;
            }

            if (resH == 2) {
                Main.SetStateAI(numSpaceH, i,  simbol);
                return true;
            }

            resD1 = Main.GetRes(i, i, simb);
            resDiag1 += resD1;
            if( resD1 == 0 )  rD1 = i;
            resD2 = Main.GetRes(2 - i, i, simb);
            resDiag2 += resD2;
            if( resD2 == 0 )  rD2 = i;
        }

        if (resDiag1 == 2) {
            Main.SetStateAI( rD1, rD1,  simbol);
            return true;
        }

        if (resDiag2 == 2) {
            Main.SetStateAI(  2 - rD2, rD2, simbol);
            return true;
        }

        return false;
    }


    @Override
    void makeMove(  )  {


        if( !checkAndSet( simbol )) {
            if( !checkAndSet( Main.negateSign(simbol) ) )
                super.makeMove();
        }
    }
    @Override
    void moving(  ) {
        makeMove(  );
        System.out.println("Making move level \"medium\"");
    }
}

class AIPlayerHard extends Player   {
    AIPlayerHard(char simb) {
        super(simb);
        //System.out.println("Create User");
    }

    ArrayList<Integer>  getFreeSpots() {
       ArrayList<Integer>  free = new ArrayList<Integer>();
       for( int i = 0; i <  Main.fieldCH.length ; ++i ) {
           if( Main.fieldCH[i] == '_' )
               free.add(i);
       }
       return free;
    }

    int  maxiMinMove( char player ) {
        //char[]  newField = Arrays.copyOf(Main.fieldCH,Main.fieldCH.length);
        ArrayList<Integer> freeSpots = getFreeSpots();
        if( Main.CheckState(simbol) ) {
            return 10;
        } else if( Main.CheckState(Main.negateSign(simbol) ) ) {
            return -10;
        } else if( freeSpots.size() == 0 ) {
            return 0;
        }
        //
        ArrayList<Integer> movePos = new ArrayList<Integer>();
        ArrayList<Integer> moveScore = new ArrayList<Integer>();
        for( int num : freeSpots ) {
            Main.fieldCH[num] = player;
            int result = maxiMinMove( Main.negateSign(player) );
            Main.fieldCH[num] = '_';
            //
            movePos.add(num);
            moveScore.add(result);
        }
        //
        int bestMove = 0;
        if( player == simbol) {
            int score = -100;
            for( int i = 0 ; i < moveScore.size() ; ++i ) {
               if( moveScore.get(i) > score )  {
                   score = moveScore.get(i);
                   bestMove = movePos.get(i);
               }
            }
        } else {
            int score = 100;
            for( int i = 0 ; i < moveScore.size() ; ++i ) {
                if( moveScore.get(i) < score )  {
                    score = moveScore.get(i);
                    bestMove = movePos.get(i);
                }
            }
        }
        return bestMove;
    }

    @Override
    void makeMove(  ) {
        int move = maxiMinMove( simbol );
        Main.fieldCH[move] = simbol;
    }

    @Override
    void moving(  ) {
        makeMove(  );
        System.out.println("Making move level \"hard\"");
    }
}


class UserPlayer extends Player  {
    UserPlayer( char simb ) {
        super(simb);
        //System.out.println("Create User");
    }

    @Override
    void moving(  ) {

        makeMove(  );
    }

    @Override
    void makeMove(  )  {
        boolean cont = true;
        int row = 0;
        int col = 0;
        while( cont ) {
            System.out.print("Enter the coordinates: ");
            String[] sz = Main.scan.nextLine().split(" ");
            try {
                col = Integer.parseInt(sz[0]);
                if( sz.length > 1 )
                    row = Integer.parseInt(sz[1]);
            }
            catch( NumberFormatException e ) {
                System.out.println("You should enter numbers!");
            }

            switch(Main.GetStateUser( row, col)) {
                case '_' :
                    Main.SetStateUser(row, col, simbol);
                    cont = false;
                    break;
                case 'X' :
                case 'O' :
                    System.out.println("This cell is occupied! Choose another one!");
                    break;
                case 0 :
                    System.out.println("Coordinates should be from 1 to 3!");
                    break;
            }

        }
    }
}

public class Main {
    static void DrawState( ) {
        System.out.println("---------");
        System.out.format("| %c %c %c |\n", fieldCH[0] == '_' ? ' ' : fieldCH[0],
                fieldCH[1] == '_' ? ' ' : fieldCH[1],  fieldCH[2] == '_' ? ' ' : fieldCH[2]);
        System.out.format("| %c %c %c |\n", fieldCH[3] == '_' ? ' ' : fieldCH[3],
                fieldCH[4] == '_' ? ' ' : fieldCH[4],  fieldCH[5] == '_' ? ' ' : fieldCH[5]);
        System.out.format("| %c %c %c |\n", fieldCH[6] == '_' ? ' ' : fieldCH[6],
                fieldCH[7] == '_' ? ' ' : fieldCH[7],  fieldCH[8] == '_' ? ' ' : fieldCH[8]);
        System.out.println("---------");
    }

    static char negateSign( char sign ) {
        return sign == 'X' ? 'O' : 'X';
    }

    static int GetRes( int col, int row, char sign ) {
        if( row < 0 || row >= 3 || col < 0 || col >= 3 )
            return 0;
        return fieldCH[ col + row*3]== sign ? 1 :
                (fieldCH[col + row*3] == negateSign( sign ) ? -1 : 0);
    }


    static int GetRes( int col, int row ) {
        if( row < 0 || row > 3 || col < 0 || col >= 3 )
            return 0;
        // System.out.println("GetState  pos : " + (3*(3-row) + col - 1));
        return fieldCH[ col + row*3]== 'X' ? 1 :
                (fieldCH[col + row*3] == 'O' ? -1 : 0);
    }

    static boolean CheckState( char player ) {
        if (
                (fieldCH[0] == player && fieldCH[1] == player && fieldCH[2] == player) ||
                (fieldCH[3] == player && fieldCH[4] == player && fieldCH[5] == player) ||
                (fieldCH[6] == player && fieldCH[7] == player && fieldCH[8] == player) ||
                (fieldCH[0] == player && fieldCH[3] == player && fieldCH[6] == player) ||
                (fieldCH[1] == player && fieldCH[4] == player && fieldCH[7] == player) ||
                (fieldCH[2] == player && fieldCH[5] == player && fieldCH[8] == player) ||
                (fieldCH[0] == player && fieldCH[4] == player && fieldCH[8] == player) ||
                (fieldCH[2] == player && fieldCH[4] == player && fieldCH[6] == player)
        ) {
            return true;
        } else {
            return false;
        }
    }

    static boolean CheckState( ) {
        int resV = 0, resH = 0, resDiag1 = 0, resDiag2 = 0;
        boolean finished = true;
        for( int i = 0 ; i < 3 ;  ++i ) {
            resV = 0;
            resH = 0;
            for( int  j = 0 ; j < 3 ; ++j ) {
                resV += GetRes(i,j);
                resH += GetRes(j,i);
                if( GetRes(i,j) == 0 )
                    finished = false;
            }
            if( resV == 3 || resH == 3 ) {
                System.out.println("X wins");
                return true;
            }
            if( resV == -3 || resH == -3 ) {
                System.out.println("O wins");
                return true;
            }
            resDiag1 += GetRes(i,i);
            resDiag2 += GetRes(2-i,i);
        }
        if( resDiag1 == 3 || resDiag2 == 3 ) {
            System.out.println("X wins");
            return true;
        }
        if( resDiag1 == -3 || resDiag2 == -3 ) {
            System.out.println("O wins");
            return true;
        }
        if(finished) {
            System.out.println("Draw");
            return true;
        }
        return false;
    }

    static char GetStateUser( int row, int col ) {
        if( row < 1 || row > 3 || col <1 || col > 3 )
            return 0;
        // System.out.println("GetState  pos : " + (3*(3-row) + col - 1));
        return fieldCH[ 3*(3-row) + col - 1];
    }

    static char GetStateAI( int col, int row ) {
        if( row < 0 || row >= 3 || col < 0 || col >= 3 )
            return 0;
        // System.out.println("GetState  pos : " + (3*(3-row) + col - 1));
        return fieldCH[ 3*row + col];
    }

    static boolean SetStateUser( int row, int col, char ch ) {
        if( row < 1 || row > 3 || col < 1 ||col > 3 ) {
            return false;
        }
        fieldCH[ 3*(3-row) + col - 1] = ch;
        return true;

    }

    static boolean SetStateAI( int col, int row, char ch ) {
        if( row < 0 || row > 2 || col < 0 ||col > 2 ) {
            return false;
        }
        fieldCH[ 3*row + col] = ch;
        return true;

    }


    static void GetPosition( ) {

    }

    static Player makePlayer(  String type, char simb ) {
        Player player = null;
        switch (type) {
            case "medium":
                player = new AIPlayerMedium(simb);
                break;
            case "easy":
                player = new AIPlayerSimple(simb);
                break;
            case "user":
                player = new UserPlayer(simb);
                break;
            case "hard" :
                player = new AIPlayerHard(simb);
                break;
             default :
                System.out.println("Bad command");
                break;
        }
        return player;
    }

    public static void main(String[] args) {
        while( true ) {
            boolean commandIn = false;
            while(!commandIn) {
                System.out.print("Input command: ");
                String[] command = scan.nextLine().split(" ");
                switch (command[0]) {
                    case "exit":
                        return;
                    case "start":
                        if( command.length == 3 ) {
                            if( ("easy".equals(command[1]) || "user".equals(command[1]) || "medium".equals(command[1]) || "hard".equals(command[1])) &&
                                    ("easy".equals(command[2]) || "user".equals(command[2]) || "medium".equals(command[2]) || "hard".equals(command[2])) ) {
                                player1 =  makePlayer( command[1], 'X' );
                                player2 = makePlayer( command[2], 'O' );
                                commandIn = true;
                            } else {
                                System.out.println("Bad parameters!");
                            }
                        }
                        break;
                }
            }

            Arrays.fill(fieldCH, '_');
            boolean stop = false;

            DrawState();
            while (!stop) {
                player1.moving();
                //
                DrawState();
                stop = CheckState();
                //
                if(!stop) {
                    player2.moving();
                    //
                    DrawState();
                    stop = CheckState();
                }
            }
        }
    }

    static Scanner  scan = new Scanner(System.in);
    static char[]   fieldCH = new char[9];
    static char     nextChar = 'X';
    static Player   player1 = null;
    static Player   player2 = null;
}


