/*
Mohammed Raza
CS.408 Programming Project

This skeleton is provided to help CS408 students in completing the DES implementation for the programming project in Spring 2013.
You have to fill the code to make the functions work under DES.
Please strictly follow the skeleton since the evaluation will be based on this skeleton.
You can add your own functions, but please keep the original functions. Do not change the function declarations (only add your implementation for the functions definitions).
This program adopts boolean array, for arithmetics, true = 1, false = 0; moreover, if you get a number whose binary representation is 100, then it can also be converted back to boolean array {true, false, false}
The test cases which will be used for grading are shown in the main function.
You can find the points for every step, e.g., if you finish writing function Key_Schedule, and your code can successfully pass the test, then you can get 15 points.
The total number of points is 100.
The notation in this skeleton corresponds to the DES section in Handbook of Applied Cryptography (HAC).
(http://www.cacr.math.uwaterloo.ca/hac/about/chap7.pdf, pages 252-256)
Please do not distribute this file without permission.
*/

import java.io.FileWriter;
import java.io.IOException;


public class des
{

  /*some constants used in the project*/
  private static final int KEY_NO = 20;/*Modified version of DES with 20 rounds, so we need 20 round keys*/
  private static final int ROUND_KEY_LENGTH = 48;/*every round key is 48-bit in length*/
  private static final int KEY_LENGTH = 64; /*the length of the original key, which is 64 bits, including 8 bits of parity*/
  private static final int HALF_LENGTH = 32;/*length in bits of half a DES block*/
  private static final int BLOCK_LENGTH = 64;/*length in bits of a DES block*/
  private static final int BYTE_LENGTH = 8;
  
  /*tables for DES*/
  /*note: All the tables are already hdefined, but if you prefer your own way of defining the tables, you can choose your own way and make sure that your tables are correct (as defined in the HAC book)*/
  private static final int IP[] = {58, 50, 42, 34, 26, 18, 10, 2,60, 52, 44, 36, 28, 20 ,12, 4, 
  62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
  57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3, 
  61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7}; /*corresponding to table 7.2*/
  private static final int IP_INVERSE[] = {40, 8, 48, 16, 56, 24, 64,32, 39, 7, 47, 15, 55, 23, 63, 31,
  38, 6, 46, 14, 54, 22, 62, 30,37, 5, 45, 13,53, 21, 61, 29,
  36, 4, 44, 12, 52, 20, 60, 28,35, 3, 43, 11, 51, 19, 59, 27,
  34, 2, 42, 10, 50, 18, 58, 26,33, 1, 41, 9, 49, 17, 57, 25}; /*corresponding to table 7.2*/
  private static final int E[] = {32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 
  8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 
  16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25, 
  24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32 ,1}; /*corresponding to table 7.3*/
  private static final int P[] = {16, 7, 20, 21, 29, 12, 28, 17, 
  1, 15, 23, 26, 5, 18, 31, 10, 
  2, 8, 24, 14, 32, 27, 3, 9, 
  19, 13, 30, 6, 22, 11, 4, 25}; /*corresponding to table 7.3*/
  private static final int PC1[] = {57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18, 
  10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 
  63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 
  14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4}; /*corresponding to table 7.4*/
  private static final int PC2[] = {14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 
  23, 19, 12 ,4, 26, 8, 16, 7, 27, 20, 13, 2, 
  41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 
  44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32}; /*corresponding to table 7.4*/
  private static final int S1[][] = {{14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7}, 
  {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8}, 
  {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0}, 
  {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}}; /*corresponding to table7.8*/
  private static final int S2[][] = {{15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10}, 
  {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5}, 
  {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15}, 
  {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}}; /*corresponding to table7.8*/
  private static final int S3[][] = {{10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8}, 
  {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1}, 
  {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7}, 
  {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}}; /*corresponding to table7.8*/
  private static final int S4[][] = {{7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15}, 
  {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9}, 
  {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4}, 
  {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}}; /*corresponding to table7.8*/
  private static final int S5[][] = {{2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9}, 
  {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6}, 
  {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14}, 
  {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}}; /*corresponding to table7.8*/
  private static final int S6[][] = {{12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11}, 
  {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8}, 
  {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6}, 
  {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}}; /*corresponding to table7.8*/
  private static final int S7[][] = {{4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1}, 
  {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6}, 
  {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2}, 
  {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}}; /*corresponding to table7.8*/
  private static final int S8[][] = {{13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7}, 
  {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2}, 
  {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8}, 
  {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}}; /*corresponding to table7.8*/
  
  
  /*
    DES key schedule
    generate 16 round keys from original key K
    input: 64-bit key K= k_1 ... k_64
    output: sixteen 48-bit round keys K_i, for 1<=k<=16
    15 points
  */
  public boolean[][] Key_Schedule(boolean key[])
  {
    boolean round_key_gen[][] = new boolean[KEY_NO][ROUND_KEY_LENGTH];//{{true, true},{true ,false}};//[][]={{1,2}, {2,3}};
    if(key.length != KEY_LENGTH)
    {
      System.out.printf("Key_Schedule: Wrong input. The key should be 64-bit in length\n");
      return round_key_gen;
    }
     // fill your code here, the return value should be put into round_key_gen
	  // Defining vi as 1 if i=1,2,9,or 16 and as 2 otherwise, for 20 round keys
	 
	 int vi;
	 for (int i=1; i<=20; i++) 
	 {
	 	if (i==1 || i==2 || i==9 || i==16)
		{
	 		vi = 1;
		}
		else
		{
			vi = 2;
		}
	 }
	// Splitting key[] to c0[] according to PC1 in table 7.4 
	boolean c0[] = {key[57], key[49], key[41], key[33], key[25], key[17], key[9], key[1], key[58], key[50], key[42], key[34], key[26], key[18], 
   key[10], key[2], key[59], key[51], key[43], key[35], key[27], key[19], key[11], key[3], key[60], key[52], key[44], key[36]};
  
   // Splitting key[] to d0[] according to PC1 in table 7.4
	boolean d0[] = {key[63], key[55], key[47], key[39], key[31], key[23], key[15], key[7], key[62], key[54], key[46], key[38], key[30], key[22], 
   key[14], key[6], key[61], key[53], key[45], key[37], key[29], key[21], key[13], key[5], key[28], key[20], key[12], key[4]};

 
		// Left circular shifting c0-1 
		boolean ci[] = {key[48], key[40], key[32], key[24], key[16], key[8], key[0], key[57], key[49], key[41], key[33], key[25], key[17], 
      key[9], key[1], key[58], key[50], key[42], key[34], key[26], key[18], key[10], key[2], key[59], key[51], key[43], key[56], key[35]};
		
		// Left circular shifting d0-1
		boolean di[] = {key[54], key[46], key[38], key[30], key[22], key[14], key[6], key[61], key[53], key[45], key[37], key[29], key[21], 
      key[13], key[5], key[60], key[52], key[44], key[36], key[28], key[20], key[12], key[4], key[27], key[19], key[11], key[3], key[62]};
	
		// round_key_gen = PC2[ci][di] according to PC2 in table 7.4
		
		boolean cidi[] = {key[48], key[40], key[32], key[24], key[16], key[8], key[0], key[57], key[49], key[41], key[33], key[25], key[17], 
      key[9], key[1], key[58], key[50], key[42], key[34], key[26], key[18], key[10], key[2], key[59], key[51], key[43], key[56], key[35], key[54], key[46], key[38], key[30], key[22], key[14], key[6], key[61], key[53], key[45], key[37], key[29], key[21], 
      key[13], key[5], key[60], key[52], key[44], key[36], key[28], key[20], key[12], key[4], key[27], key[19], key[11], key[3], key[62]};
		
		boolean pc2select[] = {cidi[14], cidi[17], cidi[11], cidi[24], cidi[1], cidi[5], cidi[3], cidi[28], cidi[15], cidi[6], cidi[21], cidi[10], 
 			 cidi[23], cidi[19], cidi[12] ,cidi[4], cidi[26], cidi[8], cidi[16], cidi[7], cidi[27], cidi[20], cidi[13], cidi[2], 
  			cidi[41], cidi[52], cidi[31], cidi[37], cidi[47], cidi[55], cidi[30], cidi[40], cidi[51], cidi[45], cidi[33], cidi[48], 
  			cidi[44], cidi[49], cidi[39], cidi[56], cidi[34], cidi[53], cidi[46], cidi[42], cidi[50], cidi[36], cidi[29], cidi[32]};
			
	//Storing the entire array to round_key_gen		
		for (int	j=0; j<=47; j++)
		{
			for (int i=0; i<=20; i++)
			{
				round_key_gen[i][j] = pc2select[j];
			}
		}
	
    return round_key_gen;
  
  }

  /*
    Initial Permutation
    the first step for DES
    input: the original 64-bit block
    output: the block after permutation according to IP table (table 7.2)
    10 points
  */
  public boolean[] Initial_Permutation(boolean block[])
  {
    boolean block_after_ip[] = new boolean[BLOCK_LENGTH];
    if(block.length != BLOCK_LENGTH)
    {
      System.out.printf("Initial_Permutation: Wrong input! The input block for Initial Permutation should have length  64 bits\n");
      return block_after_ip;
    }
    
    
    // fill your code here
	 // Splitting left half of block[] according to table 7.2, stored in 10[]
	 boolean l0[] = {block[58], block[50], block[42], block[34], block[26], block[18], block[10], block[2],block[60], block[52], block[44], block[36], block[28], block[20] ,block[12], block[4], 
 	 block[62], block[54], block[46], block[38], block[30], block[22], block[14], block[6], block[64], block[56], block[48], block[40], block[32], block[24], block[16], block[8]};
	 
	 // Splitting right half of block[] according to table 7.2, stored in r0[]
	 boolean r0[] = {block[57], block[49], block[41], block[33], block[25], block[17], block[9], block[1], block[59], block[51], block[43], block[35], block[27], block[19], block[11], block[3], 
    block[61], block[53], block[45], block[37], block[29], block[21], block[13], block[5], block[63], block[55], block[47], block[39], block[31], block[23], block[15], block[7]};
    
	 // Storing the entire block according to table 7.2
	 boolean fullblock[] = {block[58], block[50], block[42], block[34], block[26], block[18], block[10], block[2],block[60], block[52], block[44], block[36], block[28], block[20] ,block[12], block[4], 
 	 block[62], block[54], block[46], block[38], block[30], block[22], block[14], block[6], block[64], block[56], block[48], block[40], block[32], block[24], block[16], block[8], block[57], block[49], block[41], block[33], block[25], block[17], block[9], block[1], block[59], block[51], block[43], block[35], block[27], block[19], block[11], block[3], 
    block[61], block[53], block[45], block[37], block[29], block[21], block[13], block[5], block[63], block[55], block[47], block[39], block[31], block[23], block[15], block[7]};
	 
	 // Storing the whole block[] according to table 7.2 to block_after_ip
	 for(int i = 0; i<= 63; i++)
	 {
	 		 block_after_ip[i] = fullblock[i];
	 }

    return block_after_ip;
  }
  
  
  /*
    Expansion (function E)
    expand 32 bits to 48 bits according to E table (table 7.3), used by function f= P(S(E(R_i-1) XOR K_i))
    input: 32-bit array
    output: 48-bit array
    5 points
  */
  public boolean[] Expansion(boolean array[])
  {
    boolean array_after_expand[] = new boolean[ROUND_KEY_LENGTH];
    if(array.length != HALF_LENGTH)
    {
      System.out.printf("Expansion: Wrong input! The input arry for Expansion should have length  " +HALF_LENGTH+ " bits\n");    
      return array_after_expand;
    }
    
    
    // fill your code here
    // Storing array[] values to expand[] according to table 7.3
	 boolean expand[] = {array[32], array[1], array[2], array[3], array[4], array[5], array[4], array[5], array[6], array[7], array[8], array[9], 
    array[8], array[9], array[10], array[11], array[12], array[13], array[12], array[13], array[14], array[15], array[16], array[17], 
    array[16], array[17], array[18], array[19], array[20], array[21], array[20], array[21], array[22], array[23], array[24], array[25], 
    array[24], array[25], array[26], array[27], array[28], array[29], array[28], array[29], array[30], array[31], array[32] ,array[1]};
	 
	 // Storing values from expand[] to array_after_expand
	 for(int i = 0; i<= 47; i++)
	 {
		 array_after_expand[i] = expand[i];
	 }
  
    return array_after_expand;
  }
  
  /*
    XOR two bit-arrays, used by function f= P(S(E(R_i-1) XOR K_i)), also used by One_Round function 
    input: two bit-arrays
    output: the result of XOR
    note: make sure that array_1 and array_2 have equal length
    5 points
  */
  public boolean[] XOR(boolean array_1[],  boolean array_2[])
  {
    if(array_1.length != array_2.length)
    {
      System.out.printf("XOR: Wrong input for XOR function! Please check your input! Only support the case that the arrays are with equal length\n");
      return new boolean[1];
    }
    boolean result_xor[] = new boolean[array_1.length];
    
    
    // fill your code here
	 // XORing array_1[] and array_2[] and then storing the results in result_xor
	 for(int i = 0; i<= 47; i++)
	 {
		 result_xor[i] = (array_1[i]^array_2[i]);
	 }
    
    return result_xor;
    
  }
   
  
  /*
    SboxesSubstitution (function S), used by function f= P(S(E(R_i-1) XOR K_i)) 
    convert the 48-bit array into the 32-bit array
    input: 48-bit array
    output: 32-bit array
    15 points
  */
  public boolean[] SboxesSubstitution(boolean array[])
  {
    boolean array_after_substitution[] = new boolean[HALF_LENGTH];
    if(array.length != ROUND_KEY_LENGTH)
    {
      System.out.printf("SboxesSubstitution: Wrong input! The input arry for SboxesSubstitution should have length  " +ROUND_KEY_LENGTH+ " bits\n");
      return array_after_substitution;
    }
    
	 
	 // fill your code here
	 // Storing input array[] of 48-bits to 8, 6-bit arrays
	 boolean b1[] = {array[0],array[1],array[2],array[3],array[4],array[5]};
    boolean b2[] = {array[6],array[7],array[8],array[9],array[10],array[11]};
	 boolean b3[] = {array[12],array[13],array[14],array[15],array[16],array[17]};
	 boolean b4[] = {array[18],array[19],array[20],array[21],array[22],array[23]};
	 boolean b5[] = {array[24],array[25],array[26],array[27],array[28],array[29]};
	 boolean b6[] = {array[30],array[31],array[32],array[33],array[34],array[35]};
	 boolean b7[] = {array[36],array[37],array[38],array[39],array[40],array[41]};
	 boolean b8[] = {array[42],array[43],array[44],array[45],array[46],array[47]};
  
  	// Converting b1[indexes 0 and 5] from boolean to binary and calculating r for b1[]
  		int i1=0;
		int j1=0;
		int r1; 
  		 if (b1[0] == true)
		 	i1=1;
		 if (b1[5] == true)
		 	j1=1; 
		 r1 = 2* (i1+j1);
		 
	 // Converting b1[indexes 1 through 4] from boolean to binary and calculating c for b1[]
	   int s1=0;
		int t1=0;
		int u1=0;
		int v1=0;	
		int c1; 
		 if (b1[1] == true)
		 	s1=1;
	 	 if (b1[2] == true)
		 	t1=1;
	 	 if (b1[3] == true)
		 	u1=1;		  
	 	 if (b1[4] == true)
		 	v1=1;
		 c1 = s1+t1+u1+v1;
		 
		 // Calculating output for b1[] using table S1[], r and c for b1[]
		 int output1 = S1[r1][c1];
		 
		 // Converting the integer value to 4-bit binary
   	 boolean[] bits1 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits1[i] = (output1 & (1 << i)) != 0;
		 }
		 
		 
		 
		// Converting b2[indexes 0 and 5] from boolean to binary and calculating r for b2[]
		int i2=0;
		int j2=0;
		int r2;  
  		 if (b2[0] == true)
		 	i2=1;
		 if (b2[5] == true)
		 	j2=1;	 
		 r2 = 2* (i2+j2);
		 	
		// Converting b2[indexes 1 through 4] from boolean to binary and calculating c for b2[]		 
		int s2=0;
		int t2=0;
		int u2=0;
		int v2=0;	
		int c2; 
		 if (b2[1] == true)
		 	s2=1;
	 	 if (b2[2] == true)
		 	t2=1;
	 	 if (b2[3] == true)
		 	u2=1;		  
	 	 if (b1[4] == true)
		 	v2=1;
		 c2 = s2+t2+u2+v2;
		 
		 // Calculating output for b2[] using table S2[], r and c for b2[]
		 int output2 = S2[r2][c2];
		 
		 // Converting the integer value to 4-bit binary
   	 boolean[] bits2 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits2[i] = (output2 & (1 << i)) != 0;
		 }
		 
		 
		// Converting b3[indexes 0 and 5] from boolean to binary and calculating r for b3[]
		int i3=0;
		int j3=0;
		int r3; 
  		 if (b3[0] == true)
		 	i3=1;
		 if (b3[5] == true)
		 	j3=1;		 
		 r3 = 2* (i3+j3);
		 
		// Converting b3[indexes 1 through 4] from boolean to binary and calculating c for b3[] 
		int s3=0;
		int t3=0;
		int u3=0;
		int v3=0;	
		int c3; 
		 if (b3[1] == true)
		 	s3=1;
	 	 if (b3[2] == true)
		 	t3=1;
	 	 if (b3[3] == true)
		 	u3=1;		  
	 	 if (b3[4] == true)
		 	v3=1;
		 c3 = s3+t3+u3+v3;
		 
		 // Calculating output for b3[] using table S3[], r and c for b3[]
		 int output3 = S3[r3][c3];
		 
		 // Converting the integer value to 4-bit binary
		 boolean[] bits3 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits3[i] = (output3 & (1 << i)) != 0;
		 }
		 
		
		// Converting b4[indexes 0 and 5] from boolean to binary and calculating r for b4[] 
		int i4=0;
		int j4=0;
		int r4;
  		 if (b4[0] == true)
		 	i4=4;
		 if (b4[5] == true)
		 	j4=1;
  		 r4 = 2* (i4+j4);
		 
		// Converting b4[indexes 1 through 4] from boolean to binary and calculating c for b4[] 
		int s4=0;
		int t4=0;
		int u4=0;
		int v4=0;	
		int c4; 
		 if (b4[1] == true)
		 	s4=1;
	 	 if (b4[2] == true)
		 	t4=1;
	 	 if (b4[3] == true)
		 	u4=1;		  
	 	 if (b4[4] == true)
		 	v4=1;
		 c4 = s4+t4+u4+v4;
		 
		 // Calculating output for b4[] using table S4[], r and c for b4[]
		 int output4 = S4[r4][c4];
		 
		 // Converting the integer value to 4-bit binary
		 boolean[] bits4 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits4[i] = (output4 & (1 << i)) != 0;
		 }
		 
		 
		// Converting b5[indexes 0 and 5] from boolean to binary and calculating r for b5[] 
		int i5=0;
		int j5=0;
		int r5; 
  		 if (b5[0] == true)
		 	i5=1;
		 if (b5[5] == true)
		 	j5=1;	 
		 r5 = 2* (i5+j5);
		
		// Converting b5[indexes 1 through 4] from boolean to binary and calculating c for b5[] 
		int s5=0;
		int t5=0;
		int u5=0;
		int v5=0;	
		int c5; 
		 if (b5[1] == true)
		 	s5=1;
	 	 if (b5[2] == true)
		 	t5=1;
	 	 if (b5[3] == true)
		 	u1=1;		  
	 	 if (b5[4] == true)
		 	v5=1;
		 c5 = s5+t5+u5+v5;
		 
		 // Calculating output for b5[] using table S5[], r and c for b5[]
		 int output5 = S5[r5][c5];
		 
		 // Converting the integer value to 4-bit binary
		 boolean[] bits5 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits5[i] = (output5 & (1 << i)) != 0;
		 }
		 
		 
		// Converting b6[indexes 0 and 5] from boolean to binary and calculating r for b6[] 
		int i6=0;
		int j6=0;
		int r6;
  		 if (b6[0] == true)
		 	i6=1;
		 if (b6[5] == true)
		 	j6=1;		 
		 r6 = 2* (i6+j6);
		
		// Converting b6[indexes 1 through 4] from boolean to binary and calculating c for b6[] 
		int s6=0;
		int t6=0;
		int u6=0;
		int v6=0;	
		int c6; 
		 if (b6[1] == true)
		 	s6=1;
	 	 if (b6[2] == true)
		 	t6=1;
	 	 if (b6[3] == true)
		 	u6=1;		  
	 	 if (b6[4] == true)
		 	v6=1;
		 c6 = s6+t6+u6+v6;
		 
		 // Calculating output for b6[] using table S6[], r and c for b6[]
		 int output6 = S6[r6][c6];
		 
		 // Converting the integer value to 4-bit binary
		 boolean[] bits6 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits6[i] = (output6 & (1 << i)) != 0;
		 }
		 
		 
		// Converting b7[indexes 0 and 5] from boolean to binary and calculating r for b7[] 
		int i7=0;
		int j7=0;
		int r7; 
  		 if (b7[0] == true)
		 	i7=1;
		 if (b7[5] == true)
		 	j7=1;		 
		 r7 = 2* (i7+j7);
		
		// Converting b7[indexes 1 through 4] from boolean to binary and calculating c for b7[] 
		int s7=0;
		int t7=0;
		int u7=0;
		int v7=0;	
		int c7; 
		 if (b7[1] == true)
		 	s7=1;
	 	 if (b7[2] == true)
		 	t7=1;
	 	 if (b7[3] == true)
		 	u7=1;		  
	 	 if (b7[4] == true)
		 	v7=1;
		 c7 = s7+t7+u7+v7;
		 
		 // Calculating output for b7[] using table S7[], r and c for b7[]
		 int output7 = S7[r7][c7];
		 
		 // Converting the integer value to 4-bit binary
		 boolean[] bits7 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits7[i] = (output7 & (1 << i)) != 0;
		 }
		 
		 
		// Converting b8[indexes 0 and 5] from boolean to binary and calculating r for b8[] 
		int i8=0;
		int j8=0;
		int r8; 
  		 if (b8[0] == true)
		 	i8=1;
		 if (b8[5] == true)
		 	j8=1;	 
		 r8 = 2* (i8+j8);
		
		// Converting b8[indexes 1 through 4] from boolean to binary and calculating c for b8[]
		int s8=0;
		int t8=0;
		int u8=0;
		int v8=0;	
		int c8; 
		 if (b8[1] == true)
		 	s8=1;
	 	 if (b8[2] == true)
		 	t8=1;
	 	 if (b8[3] == true)
		 	u8=1;		  
	 	 if (b8[4] == true)
		 	v8=1;
		 c8 = s8+t8+u8+v8;
		 
		 // Calculating output for b8[] using table S8[], r and c for b8[]
		 int output8 = S8[r8][c8];
		 
		 // Converting the integer value to 4-bit binary
		 boolean[] bits8 = new boolean[4];
   	 for (int i=3; i>=0; i--) 
		 {
        bits8[i] = (output8 & (1 << i)) != 0;
		 }
	
		// Storing the binary outputs to temparray[], each is a 4-bit boolean array
		boolean temparray[] = {bits1[0],bits1[1],bits1[2],bits1[3],
									  bits2[0],bits2[1],bits2[2],bits2[3],
									  bits3[0],bits3[1],bits3[2],bits3[3],
									  bits4[0],bits4[1],bits4[2],bits4[3],
									  bits5[0],bits5[1],bits5[2],bits5[3],
									  bits6[0],bits6[1],bits6[2],bits6[3],
									  bits7[0],bits7[1],bits7[2],bits7[3],
									  bits8[0],bits8[1],bits8[2],bits8[3]};
	 
	 // Storing the values from temparray[] to array_after_substitution							  
	 for (int i=0; i<=31; i++)
	 {
	 	array_after_substitution[i] = temparray[i];
	 }
 	     
    return array_after_substitution; 
  
  }
  
  /*
    Permutation (function P), used by function f= P(S(E(R_i-1) XOR K_i))
    permute the array after substitution according to table P (table 7.3)
    input: 32-bit array
    output: 32-bit array
    5 points
  */
  public boolean[] Permutation_f(boolean array[])
  {
    boolean array_after_permutation[] = new boolean[HALF_LENGTH];
    if(array.length != HALF_LENGTH)
    {
      System.out.printf("Permutation: Wrong input! The input arry for Permutation should have length  " +HALF_LENGTH+ " bits\n");
      return array_after_permutation;
    }
    
    
    // fill your code here
	 // Storing input array[] according to table 7.3 in temparray[]
	 boolean temparray1[] = {array[16], array[7], array[20], array[21], array[29], array[12], array[28], array[17], 
  									 array[1], array[15], array[23], array[26], array[5], array[18], array[31], array[10], 
  									 array[2], array[8], array[24], array[14], array[32], array[27], array[3], array[9], 
  									 array[19], array[13], array[30], array[6], array[22], array[11], array[4], array[25]};
	 
	 // Storing temparray[] in array_after_permutation as output
	 for(int i = 0; i<= 31; i++)
	 {
	 		 array_after_permutation[i] = temparray1[i];
	 }
	 
     return array_after_permutation;
  }
  
  
  /*
    one round in DES
    input: 64-bit array (L_i-1, R_i-1) and the corresponding round key K_i
    output: 64-bit array (L_i, R_i), and L_i = R_i-1, R_i = L_i-1 XOR f(R_i-1, K_i) = L_i-1 XOR P(S(E(R_i-1) XOR K_i))
    15 points
  
  */
  public boolean[] One_Round(boolean block[], boolean round_key[])
  {
    
    boolean block_after_one_round[] = new boolean[BLOCK_LENGTH];
    if(block.length != BLOCK_LENGTH)
    {
      System.out.printf("One_Round: Wrong input! The input block for one round should have length  64 bits\n");
      return block_after_one_round;
    }
    if(round_key.length != ROUND_KEY_LENGTH)
    {
      System.out.printf("One_Round: Wrong input! The round key should have length  "+ ROUND_KEY_LENGTH+" bits\n");
      return block_after_one_round;
    }
    
    
    // fill your code here
	 
	 	// Storing li-1 array
 		boolean liminus1[] = {block[0],block[1],block[2],block[3],block[4],block[5],block[6],block[7],block[8],block[9],block[10],
										block[11],block[12],block[13],block[14],block[15],block[16],block[17],block[18],block[19],block[20],block[21],
										block[22],block[23],block[24],block[25],block[26],block[27],block[28],block[29],block[30],block[31]};
	  // Storing ri-1 array
	 	boolean riminus1[] = {block[32],block[33],block[34],block[35],block[36],block[37],block[38],block[39],block[40],block[42],block[42],
										block[43],block[44],block[45],block[46],block[47],block[48],block[49],block[50],block[51],block[52],block[53],
										block[54],block[55],block[56],block[57],block[58],block[59],block[60],block[61],block[62],block[63]};
		
		// Storing li[]							
		boolean li[] = new boolean[32];																		
		for(int i=0; i<=31; i++)
		{
			li[i] = riminus1[i];
		
		}
		
			boolean temp1[] = Expansion(riminus1); //expand 32 bits of riminus1 to 48 bits, Store in temp1[]
	
		   boolean temp2[] = SboxesSubstitution(temp1); // sboxsub takes 48 bits, outputs 32 bits
		
		   boolean temp3[] = Permutation_f(temp2); // permutation takes 32 bits, outputs 32 bits
			
			// f function
			boolean f[] = new boolean[32];
			for(int i=0; i<=31; i++)
			{
	    	  f[i] = temp3[i]^round_key[i];		// xor 32 bits from temp3 with given roundkey values
			}
			
			//Computing ri
			boolean ri[] = new boolean[32];
			for(int i=0; i<=31; i++)
			{
				ri[i] = liminus1[i]^f[i];
			}
	
			// Storing left and right halves of the array to tempblock[]
			boolean tempblock[] = {li[0],li[1],li[2],li[3],li[4],li[5],li[6],li[7],li[8],li[9],li[10],
										li[11],li[12],li[13],li[14],li[15],li[16],li[17],li[18],li[19],li[20],li[21],
										li[22],li[23],li[24],li[25],li[26],li[27],li[28],li[29],li[30],li[31],
										ri[0],ri[1],ri[2],ri[3],ri[4],ri[5],ri[6],ri[7],ri[8],ri[9],ri[10],
										ri[11],ri[12],ri[13],ri[14],ri[15],ri[16],ri[17],ri[18],ri[19],ri[20],ri[21],
										ri[22],ri[23],ri[24],ri[25],ri[26],ri[27],ri[28],ri[29],ri[30],ri[31]};
										
		// Storing tempblock to ock_after_one_round								
		for(int i=0; i<=63; i++)
		{
			block_after_one_round[i] = tempblock[i];
		}
			
    
    return block_after_one_round;
  
  }
  
  /*
    Inverse IP
    the final step for DES, final cyphertext will be generated after applying Inverse IP
    input: the 64-bit block after 16 rounds
    output: the block after permuation according to IP^-1 table (table 7.2)
    5 points
  */
  public boolean[] Inverse_IP(boolean block[])
  {
    boolean block_inverse_ip[] = new boolean[BLOCK_LENGTH];
    if(block.length != BLOCK_LENGTH)
    {
      System.out.printf("Inverse_IP: Wrong input! The input block for Inverse IP should have length  64 bits\n");
      return block_inverse_ip;
    }
    
    // fill your code here
	 // input is the block before inverse ip, according to table 7.2
  boolean inverseip[] = {block[40], block[8], block[48], block[16], block[56], block[24], block[64], block[32], block[39], block[7], block[47], block[15], block[55], block[23], block[63], block[31],
  block[38], block[6], block[46], block[14], block[54], block[22], block[62], block[30], block[37], block[5], block[45], block[13], block[53], block[21], block[61], block[29],
  block[36], block[4], block[44], block[12], block[52], block[20], block[60], block[28], block[35], block[3], block[43], block[11], block[51], block[19], block[59], block[27],
  block[34], block[2], block[42], block[10], block[50], block[18], block[58], block[26], block[33], block[1], block[41], block[9], block[49], block[17], block[57], block[25]};
    
	 // Storing output in block_inverse_ip
	 	 for(int i = 0; i<= 63; i++)
		 {
	 		 block_inverse_ip[i] = inverseip[i];
		 }
  
    return block_inverse_ip;
  }
  
  /*
  encryption
  encrypt a 64-bit block using DES
  input: 64-bit plaintext, 64-bit key
  output: 64-bit cyphertext
  10 points
  */
  public boolean[] encryption_DES(boolean block[], boolean key[])
  {
    boolean cypher_text[]= new boolean[BLOCK_LENGTH];
    if(block.length != BLOCK_LENGTH)
    {
      System.out.printf("encryption_DES: Wrong input! The input block for DES encryption should have length  64 bits\n");
      return cypher_text;
    }
    if(key.length != BLOCK_LENGTH)
    {
      System.out.printf("encryption_DES: Wrong input! The key for DES encryption should have length  64 bits\n");
      return cypher_text;
    }
    
    // fill your code here
	  
	 // Encrypting with One_Round 20 times
	for (int i=1; i<=20; i++)
	{
		  boolean cypher_text_temp[] = One_Round(block,key);
		  return cypher_text_temp;		 
	}	
	
	// Storing each value to cypher_text_temp
	boolean cypher_text_temp[] = new boolean[64];		
	for(int i=0; i<=63; i++)
	{	
		cypher_text_temp[i] = cypher_text_temp[i];
	}
		
	// Swapping left and right halves for the final round
	 boolean swapped[] = {cypher_text_temp[32],cypher_text_temp[33],cypher_text_temp[34],cypher_text_temp[35],cypher_text_temp[36],cypher_text_temp[37],cypher_text_temp[38],cypher_text_temp[39],cypher_text_temp[40],cypher_text_temp[42],cypher_text_temp[42],
										cypher_text_temp[43],cypher_text_temp[44],cypher_text_temp[45],cypher_text_temp[46],cypher_text_temp[47],cypher_text_temp[48],cypher_text_temp[49],cypher_text_temp[50],cypher_text_temp[51],cypher_text_temp[52],cypher_text_temp[53],
										cypher_text_temp[54],cypher_text_temp[55],cypher_text_temp[56],cypher_text_temp[57],cypher_text_temp[58],cypher_text_temp[59],cypher_text_temp[60],cypher_text_temp[61],cypher_text_temp[62],cypher_text_temp[63],cypher_text_temp[0],cypher_text_temp[1],cypher_text_temp[2],cypher_text_temp[3],cypher_text_temp[4],cypher_text_temp[5],cypher_text_temp[6],cypher_text_temp[7],cypher_text_temp[8],cypher_text_temp[9],cypher_text_temp[10],
										cypher_text_temp[11],cypher_text_temp[12],cypher_text_temp[13],cypher_text_temp[14],cypher_text_temp[15],cypher_text_temp[16],cypher_text_temp[17],cypher_text_temp[18],cypher_text_temp[19],cypher_text_temp[20],cypher_text_temp[21],
										cypher_text_temp[22],cypher_text_temp[23],cypher_text_temp[24],cypher_text_temp[25],cypher_text_temp[26],cypher_text_temp[27],cypher_text_temp[28],cypher_text_temp[29],cypher_text_temp[30],cypher_text_temp[31]};

	// Applying inverse of IP and storing in cypher_text
   boolean inverse_the_swap[] = Inverse_IP(swapped);
	
		for(int i=0; i<=63; i++)
		{
			cypher_text[i] = inverse_the_swap[i];
		}
		
   return cypher_text;
    
  }
  
  
  /*
  decryption
  decrypt a 64-bit block using DES
  input: 64-bit cyphertext, 64-bit key
  output: 64-bit plaintext
  10 points
  */
  public boolean[] decryption_DES(boolean block[], boolean key[])
  {
    boolean plain_text[]= new boolean[BLOCK_LENGTH];
    if(block.length != BLOCK_LENGTH)
    {
      System.out.printf("decryption_DES: Wrong input! The input block for DES decryption should have length  64 bits\n");
      return plain_text;
    }
    if(key.length != BLOCK_LENGTH)
    {
      System.out.printf("decryption_DES: Wrong input! The key for DES decryption should have length  64 bits\n");
      return plain_text;
    }
    
    
    // fill your code here
	 // Decryption is the same as encryption, only the order of round keys is reversed
	 // Decrypting with One_Round 20 times
	 	for (int i=1; i<=20; i++)
		{
		  boolean plain_text_temp[] = One_Round(block,key);
		  return plain_text_temp;		 
		}	
	// Storing each value to plain_text_temp
	boolean plain_text_temp[] = new boolean[64];		
	for(int i=0; i<=63; i++)
	{	
		plain_text_temp[i] = plain_text_temp[i];
	}
		
	// Swapping left and right halves for the final round
	 boolean swapped[] = {plain_text_temp[32],plain_text_temp[33],plain_text_temp[34],plain_text_temp[35],plain_text_temp[36],plain_text_temp[37],plain_text_temp[38],plain_text_temp[39],plain_text_temp[40],plain_text_temp[42],plain_text_temp[42],
										plain_text_temp[43],plain_text_temp[44],plain_text_temp[45],plain_text_temp[46],plain_text_temp[47],plain_text_temp[48],plain_text_temp[49],plain_text_temp[50],plain_text_temp[51],plain_text_temp[52],plain_text_temp[53],
										plain_text_temp[54],plain_text_temp[55],plain_text_temp[56],plain_text_temp[57],plain_text_temp[58],plain_text_temp[59],plain_text_temp[60],plain_text_temp[61],plain_text_temp[62],plain_text_temp[63],plain_text_temp[0],plain_text_temp[1],plain_text_temp[2],plain_text_temp[3],plain_text_temp[4],plain_text_temp[5],plain_text_temp[6],plain_text_temp[7],plain_text_temp[8],plain_text_temp[9],plain_text_temp[10],
										plain_text_temp[11],plain_text_temp[12],plain_text_temp[13],plain_text_temp[14],plain_text_temp[15],plain_text_temp[16],plain_text_temp[17],plain_text_temp[18],plain_text_temp[19],plain_text_temp[20],plain_text_temp[21],
										plain_text_temp[22],plain_text_temp[23],plain_text_temp[24],plain_text_temp[25],plain_text_temp[26],plain_text_temp[27],plain_text_temp[28],plain_text_temp[29],plain_text_temp[30],plain_text_temp[31]};
	
	// Applying inverse of IP and storing in plain_text
   boolean inverse_the_swap[] = Inverse_IP(swapped);
	
		for(int i=0; i<=63; i++)
		{
			plain_text[i] = inverse_the_swap[i];
		}
	
    return plain_text;
    
  }
  
  /*
    documentation
    You should provide clear documentation (comments) for your program, so that your program can easily be read by others
    5 points
  */
  
  
  /*
    show the actual bit value for the corresponding boolean array
    e.g., the boolean array {true, true, false}, then the output will be 110 
  */
  public void showBooleanArray(boolean array[])
  {
    int i = 0;
    for(i=0;i<array.length;i++)
    {
      if(array[i])
      {
        System.out.printf("1");
      }
      else System.out.printf("0");
      
      if((i+1) % BYTE_LENGTH == 0) System.out.printf(" ");
      
    }
  
  }
  
  /*
    write the actual bit value for the corresponding boolean array to output file
    e.g., the boolean array {true, true, false}, then we will write 110 to file 
  */
  public void writeBooleanArrayToFile(FileWriter fw, boolean array[])
  {
    int i = 0;
    
    try
    {
      for(i=0;i<array.length;i++)
      {
        if(array[i])
        {
//        System.out.printf("1");
          fw.write("1");
        }
        else fw.write("0");//System.out.printf("0");
      
        if((i+1) % BYTE_LENGTH == 0) fw.write(" ");//System.out.printf(" ");
      }
    }
    catch(IOException e)
    {
      System.out.printf("Exception when writing the output: "+e.toString()+"\n");
    }
  
  }
  
  /*
    get boolean array for the input bit string
    e.g., the input string is 1001, then you will get a boolean string {true, false, false, true}
  */
  public boolean[] getBooleanArray(String input)
  {
    int i = 0;
    int length = input.length();
    boolean array[] = new boolean[length];
    
    for(i=0;i<length;i++)
    {
      if(input.charAt(i) == '1')
        array[i] = true;
      else array[i] = false;  
    }
  
    return array;
  }
  
  
  /*
    The main function will be used by the grader to test the functions you have written. 
    note: You don't need to fill any code in main function. 
    
    usage: java des args[0] args[1] args[2] args[3] args[4] args[5] args[6] args[7] args[8] args[9], all of args[*] should be bit string, e.g., 1110001100
    args[0] stores the 64-bit key 
    args[1] stores the plain text (64 bits) to be encrypted, which is also used for testing Initial Permutation
    args[2] stores the test input (32 bits) for Expansion function.
    args[3] and args[4] store the test input (48 bits) for XOR function.
    args[5] stores the test input (48 bits) for SboxesSubstitution function.
    args[6] stores the test input (32 bits) for Permutation function.
    args[7] stores the test content (64 bits) for one round, while args[8] stores the corresponding round key (48 bits)
    args[9] stores the test input (64 bits) for Inverse IP.
    
  */
  public static void main(String[] args)
  {
    int i = 0;
    
    des d = new des();
    
    FileWriter fw;
    try
    {
      fw = new FileWriter("output.txt"); 
    
    
      System.out.printf("\n\nStart testing ... \n\n");
      fw.write("\n\nStart testing ... \n\n");
    
      /*test DES key schedule*/
      System.out.printf("Testing DES key schedule ...\n");
      fw.write("Testing DES key schedule ...\n");
      System.out.printf("The key for testing is: "+args[0]+"\n");
      fw.write("The key for testing is: "+args[0]+"\n");
      boolean gen_key[][] = d.Key_Schedule(d.getBooleanArray(args[0]));
      System.out.printf("The round keys generated are: \n");
      for(i=1;i<=KEY_NO;i++)
      {
        System.out.print("round key"+i+": ");
        fw.write("round key"+i+": ");
        d.showBooleanArray(gen_key[i-1]);
        d.writeBooleanArrayToFile(fw, gen_key[i-1]);
        System.out.printf("\n");
        fw.write("\n");
      }
      System.out.printf("\n");
      fw.write("\n");
    
    
      /*test Initial Permutation*/
      System.out.printf("Testing Initial Permutation ...\n");
      fw.write("Testing Initial Permutation ...\n");
      System.out.printf("The block for testing is: "+args[1]+"\n");
      fw.write("The block for testing is: "+args[1]+"\n");
      boolean block[]= d.Initial_Permutation(d.getBooleanArray(args[1]));
      System.out.printf("The block after Initial Permutation is: ");
      fw.write("The block after Initial Permutation is: ");
      d.showBooleanArray(block);
      d.writeBooleanArrayToFile(fw, block);
      System.out.print("\n\n");
      fw.write("\n\n");
    
   
    
      /*test Expansion*/
      System.out.printf("Testing Expansion Function ...\n");
      fw.write("Testing Expansion Function ...\n");
      System.out.printf("The bit string for testing is: "+args[2]+"\n");
      fw.write("The bit string for testing is: "+args[2]+"\n");
      boolean array_after_expansion[]= d.Expansion(d.getBooleanArray(args[2]));
      System.out.printf("The bit string after Expansion is: \n");
      fw.write("The bit string after Expansion is: \n");
      d.showBooleanArray(array_after_expansion);
      d.writeBooleanArrayToFile(fw, array_after_expansion);
      System.out.print("\n\n");
      fw.write("\n\n");
    
      /*test XOR*/
      System.out.printf("Testing XOR Function ...\n");
      fw.write("Testing XOR Function ...\n");
      System.out.printf("The bit strings for testing are: "+args[3]+" and "+args[4]+"\n");
      fw.write("The bit strings for testing are: "+args[3]+" and "+args[4]+"\n");
      boolean array_xor[]= d.XOR(d.getBooleanArray(args[3]),  d.getBooleanArray(args[4]));
      System.out.printf("The result after XOR is: ");
      fw.write("The result after XOR is: ");
      d.showBooleanArray(array_xor);
      d.writeBooleanArrayToFile(fw, array_xor);
      System.out.print("\n\n");
      fw.write("\n\n");
    
      /*test SboxesSubstitution*/
      System.out.printf("Testing SboxesSubstitution Function ...\n");
      fw.write("Testing SboxesSubstitution Function ...\n");
      System.out.printf("The bit string for testing is: "+args[5]+"\n");
      fw.write("The bit string for testing is: "+args[5]+"\n");
      boolean array_after_substitution[]= d.SboxesSubstitution(d.getBooleanArray(args[5]));
      System.out.printf("The bit string after Substitution is: ");
      fw.write("The bit string after Substitution is: ");
      d.showBooleanArray(array_after_substitution);
      d.writeBooleanArrayToFile(fw, array_after_substitution);
      System.out.print("\n\n");
      fw.write("\n\n");
    
      /*test Permutation*/
      System.out.printf("Testing Permutation Function ...\n");
      fw.write("Testing Permutation Function ...\n");
      System.out.printf("The bit string for testing is: "+args[6]+"\n");
      fw.write("The bit string for testing is: "+args[6]+"\n");
      boolean array_after_permutation[]= d.Permutation_f(d.getBooleanArray(args[6]));
      System.out.printf("The bit string after Permutation is: ");
      fw.write("The bit string after Permutation is: ");
      d.showBooleanArray(array_after_permutation);
      d.writeBooleanArrayToFile(fw, array_after_permutation);
      System.out.print("\n\n");
      fw.write("\n\n");
    
    
      /*test one round*/
      System.out.printf("Testing One_Round Function ...\n");
      fw.write("Testing One_Round Function ...\n");
      System.out.printf("The test input for one round is: "+args[7]+"\n");
      fw.write("The test input for one round is: "+args[7]+"\n");
      System.out.printf("The corresponding round key is: "+args[8]+"\n");
      fw.write("The corresponding round key is: "+args[8]+"\n");
      boolean block_one_round[]= d.One_Round(d.getBooleanArray(args[7]), d.getBooleanArray(args[8]));
      System.out.printf("The result of one round: ");
      fw.write("The result of one round: ");
      d.showBooleanArray(block_one_round);
      d.writeBooleanArrayToFile(fw, block_one_round);
      System.out.print("\n\n");
      fw.write("\n\n");
   
      /*test Inverse Initial Permutation*/
      System.out.printf("Testing Inverse Initial Permutation ...\n");
      fw.write("Testing Inverse Initial Permutation ...\n");
      System.out.printf("The bit string for testing is: "+args[9]+"\n");
      fw.write("The bit string for testing is: "+args[9]+"\n");
      boolean block_after_inverse_ip[]= d.Inverse_IP(d.getBooleanArray(args[9]));
      System.out.printf("The bit string after Inverse Initial Permutation is: ");
      fw.write("The bit string after Inverse Initial Permutation is: ");
      d.showBooleanArray(block_after_inverse_ip);
      d.writeBooleanArrayToFile(fw, block_after_inverse_ip);
      System.out.print("\n\n");
      fw.write("\n\n");
    
      /*test DES encryption*/
      System.out.printf("Testing encryption Function ...\n");
      fw.write("Testing encryption Function ...\n");
      System.out.printf("The plain text to be encrypted is: "+args[1]+"\n");
      fw.write("The plain text to be encrypted is: "+args[1]+"\n");
      System.out.printf("The key for encryption is: "+args[0]+"\n");
      fw.write("The key for encryption is: "+args[0]+"\n");
      boolean cypher_text[]= d.encryption_DES(d.getBooleanArray(args[1]), d.getBooleanArray(args[0]));
      System.out.printf("The cypher text is: ");
      fw.write("The cypher text is: ");
      d.showBooleanArray(cypher_text);
      d.writeBooleanArrayToFile(fw, cypher_text);
      System.out.print("\n\n");
      fw.write("\n\n");
    
      /*test DES decryption*/
      System.out.printf("Testing decryption Function ...\n");
      fw.write("Testing decryption Function ...\n");
      System.out.printf("The cypher text to be decrypted is: ");
      fw.write("The cypher text to be decrypted is: ");
      d.showBooleanArray(cypher_text);
      System.out.print("\n");
      fw.write("\n");
      System.out.printf("The key for decryption is: "+args[0]+"\n");
      fw.write("The key for decryption is: "+args[0]+"\n");
      boolean plain_text[]= d.decryption_DES(cypher_text, d.getBooleanArray(args[0]));
      System.out.printf("Decrypt and get the plain text: ");
      fw.write("Decrypt and get the plain text: ");
      d.showBooleanArray(plain_text);
      d.writeBooleanArrayToFile(fw, plain_text);
      System.out.print("\n\n");
      fw.write("\n\n");
    
    
      // close the output file
      fw.close();
    }
    catch(IOException e)
    {
      System.out.printf("Output file exception: "+e.toString()+"\n");
    }
  }

}