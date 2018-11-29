/*
 * Class that defines the agent function.
 *
 * Written by James P. Biagioni (jbiagi1@uic.edu)
 * for CS511 Artificial Intelligence II
 * at The University of Illinois at Chicago
 *
 * Last modified 2/19/07
 *
 * DISCLAIMER:
 * Elements of this application were borrowed from
 * the client-server implementation of the Wumpus
 * World Simulator written by Kruti Mehta at
 * The University of Texas at Arlington.
 *
 */

import java.util.*;

class AgentFunction {

	// string to store the agent's name
	// do not remove this variable
	private String agentName = "Agent Smith";

	// all of these variables are created and used
	// for illustration purposes; you may delete them
	// when implementing your own intelligent agent
	private int[] actionTable;
	private static int size;
	private Cell[][] percepts;
	private List<Cell> tovisit;
	//private List<Cell> visited;
	private Queue<Integer> actionQ;
	private int[] start;
	private int[] cur;
	private int[] target;
	private int[] wpos;
	private Random rand;
	private char direct;

	public AgentFunction(int worldSize, int[] location, char direction)
	{
		rand = new Random();
		size = worldSize;
		actionQ = new LinkedList<Integer>();
		tovisit = new ArrayList<Cell>();
		//visited = new ArrayList<Cell>();
		//relative percept map
		percepts = new Cell[size][size];
		for (int i=0;i<size;i++)
			for (int j=0;j<size;j++)
				percepts[i][j] = new Cell(i, j);
		start = location;
		direct = direction;
		cur = new int[2];
		cur[0] = start[0];
		cur[1] = start[1];
		target = new int[2];


		// this integer array will store the agent actions
		actionTable = new int[8];
		actionTable[0] = Action.GO_FORWARD;
		actionTable[1] = Action.GO_FORWARD;
		actionTable[2] = Action.GO_FORWARD;
		actionTable[3] = Action.GO_FORWARD;
		actionTable[4] = Action.TURN_RIGHT;
		actionTable[5] = Action.TURN_LEFT;
		actionTable[6] = Action.GRAB;
		actionTable[7] = Action.SHOOT;
	}

	public int process(TransferPercept tp)
	{
		if (tp.getGlitter())
			return actionTable[6];
		if (tp.getScream())
		{
			for (int i=0;i<size;i++)
				for (int j=0;j<size;j++)
				{
					percepts[i][j].wSafe = true;
					percepts[i][j].wWarn = 0;
				}
		}
		direct = tp.getDirection();
		//처음 방문한 셀일시
		if (percepts[cur[0]][cur[1]].visited == false)
		{
			percepts[cur[0]][cur[1]].visited = true;
			percepts[cur[0]][cur[1]].pSafe= true;
			percepts[cur[0]][cur[1]].wSafe= true;
		}
		tovisit.clear();
		//visited.add(percepts[cur[0]][cur[1]]);
		//현재 셀 상하좌우 방문예정 리스트에 추가
		if (cur[0]-1>=0)
			tovisit.add(percepts[cur[0]-1][cur[1]]);
		if (cur[0]+1<size)
			tovisit.add(percepts[cur[0]+1][cur[1]]);
		if (cur[1]-1>=0)
			tovisit.add(percepts[cur[0]][cur[1]-1]);
		if (cur[1]+1<size)
			tovisit.add(percepts[cur[0]][cur[1]+1]);

		if (!actionQ.isEmpty())
		{
			//이동액션큐에서 액션하나씩 dequeue해서 리턴
			int act = actionQ.remove();
			if (act == Action.GO_FORWARD)
			{
				if (direct == 'N')
					cur[0]++;
				else if (direct == 'S')
					cur[0]--;
				else if (direct == 'E')
					cur[1]++;
				else if (direct == 'W')
					cur[1]--;
			}
			return act;
		}
		else
		{
			int prob = 1;
			if (cur[0]-1<0||cur[0]+1>=size)
				prob++;
			if (cur[1]-1<0||cur[1]+1>=size)
				prob++;
			if (tp.getBreeze())
			{
				if (!percepts[cur[0]][cur[1]].breeze)
				{
					percepts[cur[0]][cur[1]].breeze = true;
					if ((cur[0]-1>=0) && !percepts[cur[0]-1][cur[1]].pSafe)
						percepts[cur[0]-1][cur[1]].pWarn+=prob;
						//percepts[cur[0]-1][cur[1]].pWarn++;
					if ((cur[0]+1<size) && !percepts[cur[0]+1][cur[1]].pSafe)
						percepts[cur[0]+1][cur[1]].pWarn+=prob;
						//percepts[cur[0]+1][cur[1]].pWarn++;
					if ((cur[1]-1>=0) && !percepts[cur[0]][cur[1]-1].pSafe)
						percepts[cur[0]][cur[1]-1].pWarn+=prob;
						//percepts[cur[0]][cur[1]-1].pWarn++;
					if ((cur[1]+1<size) && !percepts[cur[0]][cur[1]+1].pSafe)
						percepts[cur[0]][cur[1]+1].pWarn+=prob;
						//percepts[cur[0]][cur[1]+1].pWarn++;
				}
			}
			else
			{
				if (cur[0]-1>=0)
					percepts[cur[0]-1][cur[1]].pSafe = true;
				if (cur[0]+1<size)
					percepts[cur[0]+1][cur[1]].pSafe = true;
				if (cur[1]-1>=0)
					percepts[cur[0]][cur[1]-1].pSafe = true;
				if (cur[1]+1<size)
					percepts[cur[0]][cur[1]+1].pSafe = true;
			}
			if (tp.getStench())
			{
				if (!percepts[cur[0]][cur[1]].stench)
				{
					//System.out.println("first time\n");
					percepts[cur[0]][cur[1]].stench = true;
					if ((cur[0]-1>=0) && !percepts[cur[0]-1][cur[1]].wSafe)
						percepts[cur[0]-1][cur[1]].wWarn += prob;
					if ((cur[0]+1<size) && !percepts[cur[0]+1][cur[1]].wSafe)
						percepts[cur[0]+1][cur[1]].wWarn += prob;
					if ((cur[1]-1>=0) && !percepts[cur[0]][cur[1]-1].wSafe)
						percepts[cur[0]][cur[1]-1].wWarn += prob;
					if ((cur[1]+1<size) && !percepts[cur[0]][cur[1]+1].wSafe)
						percepts[cur[0]][cur[1]+1].wWarn += prob;

					//for (int i=0;i<size;i++)
						//for (int j=0;j<size;j++)
							//System.out.printf("%d,%d:%d\n", i,j,percepts[i][j].wWarn);
				}
				//else
					//System.out.println("not first time\n");
			}
			else
			{
				if (cur[0]-1>=0)
					percepts[cur[0]-1][cur[1]].wSafe = true;
				if (cur[0]+1<size)
					percepts[cur[0]+1][cur[1]].wSafe = true;
				if (cur[1]-1>=0)
					percepts[cur[0]][cur[1]-1].wSafe = true;
				if (cur[1]+1<size)
					percepts[cur[0]][cur[1]+1].wSafe = true;
			}
			//웜푸스 스캔, 화살 쏘기
			wpos = scanWumpus();
			if (wpos!= null)
			{
				System.out.printf("Wumpus found at %d,%d\n", wpos[0],wpos[1]);
				char wdirect = inline(wpos);
				if (wdirect != 'X')
				{
					System.out.printf("Arrow shot\n");
					turn(wdirect);
					actionQ.add(actionTable[7]);
					return actionQ.remove();
				}
			}

			//tovisit리스트 위험도 가장 낮은 순으로 정렬
			//pSafe&&wSafe 최우선
			//이외는 pWarn+wWarn 기준
			Collections.sort(tovisit, new Comparator<Cell>() {
				@Override
				public int compare(Cell c1, Cell c2) {
					if ((!(c1.pSafe&&c1.wSafe) && (c2.pSafe&&c2.wSafe)) || 
							(c1.pWarn+c1.wWarn>c2.pWarn+c2.wWarn))
						return 1;
					else if (((c1.pSafe&&c1.wSafe) && !(c2.pSafe&&c2.wSafe)) || 
							(c1.pWarn+c1.wWarn<c2.pWarn+c2.wWarn))
						return -1;
					//같으면 무작위성 부여
					else if(rand.nextInt()%2 == 0)
						return 1;
					else
						return -1;
				}
			});
			//최우선 셀로 moveTo, 액션큐에 액션삽입
			moveTo(tovisit.get(0).i, tovisit.get(0).j);
			System.out.printf("Going to %d,%d\n", tovisit.get(0).i, tovisit.get(0).j);
			int act = actionQ.remove();
			if (act == Action.GO_FORWARD)
			{
				if (direct == 'N')
					cur[0]++;
				else if (direct == 'S')
					cur[0]--;
				else if (direct == 'E')
					cur[1]++;
				else if (direct == 'W')
					cur[1]--;
			}
			return act;
		}
	}

	private void moveTo(int i, int j)
	{
		target[0] = i;
		target[1] = j;

		//S>N, N>S, W>E, E>W,
		if (	(i>cur[0] && direct=='S') ||
				(i<cur[0] && direct=='N') ||
				(j>cur[1] && direct=='W') ||
				(j<cur[1] && direct=='E')	)
		{
			actionQ.add(new Integer(actionTable[4]));
			actionQ.add(new Integer(actionTable[4]));
		}
		//E>N, W>S, S>E, N>W
		if (	(i>cur[0] && direct=='E') ||
				(i<cur[0] && direct=='W') ||
				(j>cur[1] && direct=='S') ||
				(j<cur[1] && direct=='N')	)
			actionQ.add(new Integer(actionTable[5]));
		//W>N, E>S, N>E, S>W
		if (	(i>cur[0] && direct=='W') ||
				(i<cur[0] && direct=='E') ||
				(j>cur[1] && direct=='N') ||
				(j<cur[1] && direct=='S')	)
			actionQ.add(new Integer(actionTable[4]));
		//else
		//N, S, E, W
		actionQ.add(new Integer(actionTable[0]));
	}

	private void turn(char direction)
	{
		//S>N, N>S, W>E, E>W,
		if (	(direction=='N' && direct=='S') ||
				(direction=='S' && direct=='N') ||
				(direction=='E' && direct=='W') ||
				(direction=='W' && direct=='E')	)
		{
			actionQ.add(new Integer(actionTable[4]));
			actionQ.add(new Integer(actionTable[4]));
		}
		//E>N, W>S, S>E, N>W
		if (	(direction=='N' && direct=='E') ||
				(direction=='S' && direct=='W') ||
				(direction=='E' && direct=='S') ||
				(direction=='W' && direct=='N')	)
			actionQ.add(new Integer(actionTable[5]));
		//W>N, E>S, N>E, S>W
		if (	(direction=='N' && direct=='W') ||
				(direction=='S' && direct=='E') ||
				(direction=='E' && direct=='N') ||
				(direction=='W' && direct=='S')	)
			actionQ.add(new Integer(actionTable[4]));
	}
	
	private int[] scanWumpus()
	{
		List<Cell> suspectList = new ArrayList<Cell>();
		int[] wpos = new int[2];
		for (int i=0;i<size;i++)
			for (int j=0;j<size;j++)
				if (percepts[i][j].wWarn >= 1)
					suspectList.add(percepts[i][j]);
		for (int i=0;i<suspectList.size();i++)
			if (suspectList.get(i).wSafe)
			{
				suspectList.remove(i);
				i--;
			}
		if (!suspectList.isEmpty())
		{
			int index = rand.nextInt(suspectList.size());
			//여러개면 랜덤추출
			wpos[0] = suspectList.get(index).i;
			wpos[1] = suspectList.get(index).j;
			return wpos;
		}
		else
			return null;
	}
	private char inline(int[] wpos)
	{
		if (wpos[1]==cur[1] && wpos[0]>cur[0])
			return 'N';
		if (wpos[1]==cur[1] && wpos[0]<cur[0])
			return 'S';
		if (wpos[0]==cur[0] && wpos[1]>cur[1])
			return 'E';
		if (wpos[0]==cur[0] && wpos[1]<cur[1])
			return 'W';
		return 'X';
	}


	
	// public method to return the agent's name
	// do not remove this method
	public String getAgentName() {
		return agentName;
	}
}
class Cell
{
	public int i;
	public int j;
	public char pit;
	public char wumpus;
	public int pWarn;
	public int wWarn;
	public boolean breeze;
	public boolean stench;
	public boolean pSafe;
	public boolean wSafe;
	public boolean visited;
	
	public Cell(int i, int j)
	{
		this.i = i;
		this.j = j;
		pit = '?';
		wumpus = '?';
		pWarn = 0;
		wWarn = 0;
		breeze = false;
		stench = false;
		pSafe = false;
		wSafe = false;
		visited = false;
	}
}
